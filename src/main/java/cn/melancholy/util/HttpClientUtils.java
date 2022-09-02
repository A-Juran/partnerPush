package cn.melancholy.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者： Juran on 2022-09-01 19:31
 * 作者博客：iit.la
 * 对 CloseableHttpClient二次封装。
 */
public class HttpClientUtils {

    /**
     * 链接时间
     */
    private static final int connectTime = 5000;

    private static CloseableHttpClient httpclient;

    private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(connectTime).setConnectTimeout(connectTime).build();

    /**
     * @param remoteUrl 请求地址。
     * @param parameter 携带参数。
     * @param encode    编码。
     * @return String   返回字符串信息。
     * @throws Exception 异常。
     */
    public static String doPost(String remoteUrl, JSONObject parameter, String encode) throws Exception {
        List<NameValuePair> nameValuePairs = setParams(parameter);

        httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(remoteUrl);
        httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setEntity(new StringEntity(parameter.toJSONString()));

        return executeRequest(requestConfig, httpPost, encode);
    }

    /**
     * @param remoteUrl 请求地址。
     * @param encode    编码。
     * @return String   返回字符串信息。
     * @throws Exception 异常。
     */
    public static String doGet(String remoteUrl, String encode) throws Exception {

        httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(remoteUrl);

        return executeRequest(requestConfig, httpGet, encode);
    }

    /**
     * 进行参数封装
     */
    public static List<NameValuePair> setParams(JSONObject parameter) {

        List<NameValuePair> params = new ArrayList<>();

        if (null != parameter) {
            for (String key: parameter.keySet()) {
                params.add(new BasicNameValuePair(key, String.valueOf(parameter.get(key))));
            }
        }
        return params;
    }

    /**
     * 封装发送信息
     */
    public static String executeRequest(RequestConfig requestConfig,
                                        HttpRequestBase httpObject,
                                        String encode) throws IOException {
        //设置字符集编码。
        if (null == encode || encode.trim().equals("")) {
            encode = "UTF-8";
        }
        String result = null;
        //设置请求和传输超时时间
        httpObject.setConfig(requestConfig);
        CloseableHttpResponse httpResp = httpclient.execute(httpObject);
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
            httpclient.close();
        }
        return result;
    }

}