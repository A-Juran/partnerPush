package cn.melancholy.taskController.task;

import cn.melancholy.config.WxConfigure;
import cn.melancholy.entity.ScheduledJob;
import cn.melancholy.util.HttpClientUtils;
import cn.melancholy.util.SpringContextUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者： Juran on 2022-09-01 19:31
 * 作者博客：iit.la
 */
@Slf4j
@Component
public class TaskRunnable implements Runnable {

    private ScheduledJob scheduledJob;

    public TaskRunnable(ScheduledJob scheduledJob) {
        this.scheduledJob = scheduledJob;
    }

    /**
     * 通过该方法实现每日推送。
     */
    @Override
    public void run() {
        Class<?> clazz;
        WxConfigure wxConfigure;
        try {
            //通过反射机制拿到Spring容器中的WxConfigure配置信息(主要从多线程当中获取spring中bean信息)
            clazz = Class.forName("cn.melancholy.config.WxConfigure");
            wxConfigure = (WxConfigure) SpringContextUtil.getBean(clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        //获取access token
        if (wxConfigure.access_token == null ||
                "".equals(wxConfigure.access_token)) {
            try {
                getAccessToken(wxConfigure);
            } catch (Exception e) {
                throw new RuntimeException("");
            }
        }

        /*
            进行信息推送
         */
        //对
        String[] split = scheduledJob.getPushContext().trim().split("\\|");

        /*
            获取城市信息
            一言短句
         */
        String baiduWeather = "https://api.map.baidu.com/weather/v1/?district_id=" + split[0] + "&data_type=all&ak=" + wxConfigure.getApp_baidu_ack();
        String baiduLocal = null;
        String yy = null;
        JSONObject baiduJsonObject;
        JSONObject yyJsonObject;
        try {
            baiduLocal = HttpClientUtils.doGet(baiduWeather, null);
            baiduJsonObject = JSONObject.parseObject(baiduLocal);
            yy = HttpClientUtils.doGet(wxConfigure.getApp_yy_tips(), null);
            yyJsonObject = JSONObject.parseObject(yy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //设置发送数据
        JSONObject sedMsgObject = new JSONObject();
        sedMsgObject.put("touser", scheduledJob.getPushWx());
        sedMsgObject.put("template_id", scheduledJob.getPushTemplate());

        JSONObject data = new JSONObject();
        /**
         * 时间
         * 城市
         * 温度
         * 恋爱时长
         * ta的生日
         * 我的生日
         * 一言
         */
        data.put("data", toJson(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                null));

        data.put("ctiy", toJson(String.valueOf(baiduJsonObject.getJSONObject("result")
                .getJSONObject("location")
                .get("name")), "#5B86E5"));

        data.put("temperature", toJson(String.valueOf(baiduJsonObject.getJSONObject("result")
                .getJSONObject("now")
                .get("temp")) + "℃", "#FFFFCC"));

        data.put("startTime", toJson(String.valueOf((new Date().getTime() - Long.parseLong(split[1])) / (24 * 3600 * 1000)),
                "#3B2667"));

        data.put("he", toJson(String.valueOf(getBirthDay(getFormatDay(Long.parseLong(split[2])))),
                "#FFB75E"));

        data.put("mine", toJson(String.valueOf(getBirthDay(getFormatDay(Long.parseLong(split[3])))),
                "#FFB75E"));

        data.put("hitokoto", toJson(String.valueOf(yyJsonObject.get("hitokoto")), "#457FCA"));

        sedMsgObject.put("data", data);

        //提示信息发送。
        try {
            String post = null;
            post = HttpClientUtils.doPost(wxConfigure.app_send_msg + wxConfigure.access_token,
                    sedMsgObject,
                    null);

            //如果token 失效或超时/重新生成token
            Map<String, Object> map = jsonChangeObject(post, "errcode", "errmsg");
            if (Integer.parseInt(String.valueOf(map.get("errcode"))) == 41006 ||
                    Integer.parseInt(String.valueOf(map.get("errcode"))) == 42001){
                getAccessToken(wxConfigure);
                HttpClientUtils.doPost(wxConfigure.app_send_msg + wxConfigure.access_token,
                        sedMsgObject,
                        null);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.info("推送成功，推送微信号为==" + scheduledJob.getPushWx());
    }

    /**
     * 设置token
     *
     * @param wxConfigure
     * @throws Exception
     */
    public void getAccessToken(WxConfigure wxConfigure) throws Exception {

        String getTokenUrl = wxConfigure.getApp_get_access_token() + "&appid=" +
                wxConfigure.getApp_id() + "&secret=" +
                wxConfigure.getApp_secret();

        String getJson = HttpClientUtils.doGet(getTokenUrl, null);
        JSONObject jsonObject = JSON.parseObject(getJson);
        String access_token = (String) jsonObject.get("access_token");

        System.out.println(access_token);

        wxConfigure.setAccess_token(access_token);
    }


    /**
     * 从Json 中获取信息
     */
    public Map<String, Object> jsonChangeObject(String json, String... keys) {
        JSONObject jsonObject = JSON.parseObject(json);
        HashMap<String, Object> hashMap = new HashMap<>();
        for (String key : keys) {
            hashMap.put(key, jsonObject.get(key));
        }
        return hashMap;
    }
    //封装json
    public JSONObject toJson(String value, String color) {
        JSONObject json = new JSONObject();
        json.put("value", value);
        if (color != null)
            json.put("color", color);//消息字体颜色
        return json;
    }

    //获得生日天数。
    public int getBirthDay(String addtime) {
        int days = 0;
        try {
            SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
            String clidate = addtime;
            Calendar cToday = Calendar.getInstance(); // 存今天
            Calendar cBirth = Calendar.getInstance(); // 存生日
            cBirth.setTime(myFormatter.parse(clidate)); // 设置生日
            cBirth.set(Calendar.YEAR, cToday.get(Calendar.YEAR)); // 修改为本年
            if (cBirth.get(Calendar.DAY_OF_YEAR) < cToday.get(Calendar.DAY_OF_YEAR)) {
                // 生日已经过了，要算明年的了
                days = cToday.getActualMaximum(Calendar.DAY_OF_YEAR) - cToday.get(Calendar.DAY_OF_YEAR);
                days += cBirth.get(Calendar.DAY_OF_YEAR);
            } else {
                // 生日还没过
                days = cBirth.get(Calendar.DAY_OF_YEAR) - cToday.get(Calendar.DAY_OF_YEAR);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    //时间戳转换时间
    public String getFormatDay(Long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

}
