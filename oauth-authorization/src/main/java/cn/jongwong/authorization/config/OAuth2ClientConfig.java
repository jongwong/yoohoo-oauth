package cn.jongwong.authorization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;


@Configuration
public class OAuth2ClientConfig {

    // 创建 OAuth2 Client 注册信息
    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration yooClient = ClientRegistration.withRegistrationId("yoohoo")
                .clientId("client1")
                .clientSecret("{bcrypt}$2a$10$JXM/SuOdTWRZoCcKJGD6LO0ZViKfmYqW7Uo0zpjubfcBYai1DxSm.")
                .scope("openid", "profile", "email")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("http://localhost:8080/oauth2/authorize")  // 必须配置
                .tokenUri("http://localhost:8080/oauth2/token")              // 必须配置
                .userInfoUri("http://localhost:8080/oauth2/userinfo")        // 必须配置
                .build();

        return new InMemoryReactiveClientRegistrationRepository(yooClient);
    }
}
