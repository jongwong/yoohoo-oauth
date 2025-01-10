package cn.jongwong.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class WeChatConfig {
    @Value("${custom-config.wechat.appid:defaultAppid}")
    private String appid;

    @Value("${custom-config.wechat.secret:defaultSecret}")
    private String secret;


}