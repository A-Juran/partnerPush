package cn.melancholy.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.Consts;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者： Juran on 2022-09-01 19:31
 * 作者博客：iit.la
 * 对 CloseableHttpClient二次封装。
 */
@Component
public class HttpClientUtils {

    /**
     * 链接时间
     */
    public static CloseableHttpClient initClient() {
        int connectTime = 5000;

        //添加连接参数
        ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).build();

        PoolingHttpClientConnectionManager  pcm = new PoolingHttpClientConnectionManager ();
        // 设置最大连接数
        pcm.setMaxTotal(100);
        // 设置每个连接的路由数
        pcm.setDefaultMaxPerRoute(10);
        //设置连接信息
        pcm.setDefaultConnectionConfig(connectionConfig);

        //设置全局请求配置,包括Cookie规范,HTTP认证,超时
        RequestConfig defaultConfig = RequestConfig.custom().setSocketTimeout(connectTime).setConnectTimeout(connectTime).build();

        return HttpClients.custom().setConnectionManager(pcm).setConnectionManagerShared(true).build();

    }


    /**
     * @param remoteUrl 请求地址。
     * @param parameter 携带参数。
     * @param encode    编码。
     * @return String   返回字符串信息。
     * @throws Exception 异常。
     */
    public static String doPost(String remoteUrl, JSONObject parameter, String encode) throws Exception {
        List<NameValuePair> nameValuePairs = setParams(parameter);

        HttpPost httpPost = new HttpPost(remoteUrl);
        StringEntity s = new StringEntity(parameter.toJSONString(), "UTF-8");
        httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setEntity(new StringEntity(parameter.toJSONString(), "UTF-8"));

        return executeRequest(httpPost, encode);
    }

    /**
     * @param remoteUrl 请求地址。
     * @param encode    编码。
     * @return String   返回字符串信息。
     * @throws Exception 异常。
     */
    public static String doGet(String remoteUrl, String encode) throws Exception {

        HttpGet httpGet = new HttpGet(remoteUrl);

        return executeRequest(httpGet, encode);
    }

    /**
     * 进行参数封装
     */
    public static List<NameValuePair> setParams(JSONObject parameter) {

        List<NameValuePair> params = new ArrayList<>();

        if (null != parameter) {
            for (String key : parameter.keySet()) {
                params.add(new BasicNameValuePair(key, String.valueOf(parameter.get(key))));
            }
        }
        return params;
    }

    /**
     * 封装发送信息
     */
    public static String executeRequest(
            HttpRequestBase httpObject,
            String encode) throws IOException {
        //设置字符集编码。
        if (null == encode || encode.trim().equals("")) {
            encode = "UTF-8";
        }
        String result = null;
        CloseableHttpClient httpClient = initClient();
        CloseableHttpResponse httpResp = httpClient.execute(httpObject);
        //如果是post请求再进行请求参数设置
        try {
            int statusCode = httpResp.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                result = EntityUtils.toString(httpResp.getEntity(), encode);
            } else {
                //通知管理员出现异常。
                System.out.println("状态码:"
                        + httpResp.getStatusLine().getStatusCode());
                System.out.println("HttpPost方式请求失败!");
            }
        } finally {
            //关闭流对象。
            httpResp.close();
            httpClient.close();
        }
        return result;
    }

}