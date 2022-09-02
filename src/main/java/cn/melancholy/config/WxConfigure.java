package cn.melancholy.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 作者： Juran on 2022-09-01 18:38
 * 作者博客：iit.la
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "wx")
public class WxConfigure {
    //wx appID
    public String app_id;
    //wx appSecret
    public String app_secret;
    //wx get-access-token
    public String app_get_access_token;
    //wx app-send-msg
    public String app_send_msg;
    //access_token
    public String access_token;
}
