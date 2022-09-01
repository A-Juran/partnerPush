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

    public String app_id;
    public String app_secret;
}
