package cn.jongwong.authorization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;

@Configuration
public class OAuth2UserServiceConfig {

    @Bean
    public OAuth2UserService oauth2UserService() {
        return new DefaultOAuth2UserService();
    }
}
