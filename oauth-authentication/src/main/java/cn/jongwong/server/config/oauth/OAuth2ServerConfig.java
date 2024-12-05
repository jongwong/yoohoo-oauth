package cn.jongwong.server.config.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

@Configuration
public class OAuth2ServerConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId("yoohoo")
                .clientId("client1")
                .clientSecret("{bcrypt}$2a$10$JXM/SuOdTWRZoCcKJGD6LO0ZViKfmYqW7Uo0zpjubfcBYai1DxSm.")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/callback")
                .scope("openid")
                .build();
        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    // Define OAuth2 Authorization Server Settings
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:8080")
                .build();
    }


    @Bean
    public KeyPair rsaKeyPair() throws Exception {
        // 使用 RSA 算法生成密钥对
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);  // 初始化为 2048 位的密钥
        return keyPairGenerator.generateKeyPair();  // 返回生成的密钥对
    }


}
