package cn.melancholy.taskController.task;

import cn.melancholy.config.WxConfigure;
import cn.melancholy.entity.ScheduledJob;
import cn.melancholy.util.HttpClientUtils;
import cn.melancholy.util.SpringContextUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        JSONObject sedMsgObject = new JSONObject();
        sedMsgObject.put("touser", scheduledJob.getPushWx());
        System.out.println(scheduledJob.getPushTemplate());
        sedMsgObject.put("template_id", scheduledJob.getPushTemplate());
        //data数据对象
        JSONObject data = new JSONObject();
        data.put("first", toJson("2022-09-01", null));
        data.put("keyword1", toJson("06月07日 19时24分", null));
        data.put("keyword2", toJson("2", null));

        sedMsgObject.put("data", data);

        System.out.println(sedMsgObject.toString() + "这是要发送的json数据");

        try {
            String post = HttpClientUtils.doPost(wxConfigure.app_send_msg + wxConfigure.access_token, sedMsgObject, null);
            System.out.println(post);
            Map<String, Object> map = jsonChangeObject(post, "errcode", "errmsg");
            if (Integer.parseInt(String.valueOf(map.get("errcode"))) == 41006)
                getAccessToken(wxConfigure);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.info("推送成功，推送公众号AppId为==" + wxConfigure.getApp_id());
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

    public JSONObject toJson(String value, String color) {
        JSONObject json = new JSONObject();
        json.put("value", value);
        if (color != null)
            json.put("color", color);//消息字体颜色
        return json;
    }

}
