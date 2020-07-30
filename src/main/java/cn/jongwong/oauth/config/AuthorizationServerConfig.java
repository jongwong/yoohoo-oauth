package cn.jongwong.oauth.config;


import cn.jongwong.oauth.oidc.MyOIDCAccessTokenConverter;
import cn.jongwong.oauth.oidc.MyOIDCJwtAccessTokenConverter;
import cn.jongwong.oauth.oidc.MyOIDCUserAuthenticationConverter;
import cn.jongwong.oauth.service.UserDetailService;
import cn.jongwong.oauth.service.UserService;
import cn.jongwong.oauth.validate.code.ValidateCodeProcessorHolder;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.keys.RsaKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static cn.jongwong.oauth.oidc.Constants.KEYSTORE_NAME;


import static cn.jongwong.oauth.oidc.Constants.USE_SIG;
import static cn.jongwong.oauth.oidc.Constants.OIDC_ALG;
import static cn.jongwong.oauth.oidc.Constants.DEFAULT_KEY_ID;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailService userDetailService;


    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    RedisAuthorizationCodeServices redisAuthorizationCodeServices;

    @Autowired
    private ValidateCodeProcessorHolder validateCodeProcessorHolder;


    @Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        accessTokenConverter.setSigningKey("test_key");
        return accessTokenConverter;
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.tokenKeyAccess("isAuthenticated()").checkTokenAccess("permitAll()"); // 获取密钥需要身份认证
    }


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("app")
                .secret(passwordEncoder.encode("app"))
                .authorizedGrantTypes("custom", "authorization_sms", "refresh_token", "authorization_code", "password")
                .accessTokenValiditySeconds(3600)
                .scopes("all")
                .redirectUris("http://www.baidu.com")
                .and()
                .withClient("app0001112223332")
                .secret(passwordEncoder.encode("app0001112223332"))
                .authorizedGrantTypes("custom", "authorization_sms", "refresh_token", "authorization_code", "password")
                .accessTokenValiditySeconds(3600)
                .scopes("openid")
                .redirectUris("http://localhost:8087/authorization_callback");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.accessTokenConverter(accessTokenConverter());
        endpoints
                .authenticationManager(authenticationManager)
                .tokenStore(jwtTokenStore())
//                .accessTokenConverter(jwtAccessTokenConverter())
                .authorizationCodeServices(redisAuthorizationCodeServices)
                .userDetailsService(userDetailService).tokenGranter(tokenGranter(endpoints));
    }

    private TokenGranter tokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
        ClientDetailsService clientDetails = endpoints.getClientDetailsService();
        AuthorizationServerTokenServices tokenServices = endpoints.getTokenServices();
        AuthorizationCodeServices authorizationCodeServices = endpoints.getAuthorizationCodeServices();
        OAuth2RequestFactory requestFactory = endpoints.getOAuth2RequestFactory();
        List<TokenGranter> tokenGranters = new ArrayList<TokenGranter>();

        tokenGranters.add(new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices,
                clientDetails, requestFactory));
        tokenGranters.add(new RefreshTokenGranter(tokenServices, clientDetails, requestFactory));
        tokenGranters.add(new ImplicitTokenGranter(tokenServices, clientDetails, requestFactory));
        tokenGranters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetails, requestFactory));
        tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices,
                clientDetails, requestFactory));
        tokenGranters.add(new CustomTokenGranter(authenticationManager, tokenServices,
                clientDetails, requestFactory));
        tokenGranters.add(new SmsTokenGranter(authenticationManager, tokenServices,
                clientDetails, requestFactory, validateCodeProcessorHolder, userService));
        return new CompositeTokenGranter(tokenGranters);
    }


    //======================================================================================
    @Bean
    public JsonWebKeySet jsonWebKeySet() throws Exception {
        //加载 keystore配置文件
        //加载 keystore配置文件
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(KEYSTORE_NAME)) {
            String keyJson = CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8));
            return new JsonWebKeySet(keyJson);
        }
    }


    /**
     * JwtAccessTokenConverter config
     *
     * @return JwtAccessTokenConverter
     * @throws Exception e
     * @since 1.1.0
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() throws Exception {
        PublicJsonWebKey publicJsonWebKey = publicJsonWebKey();
        MyOIDCJwtAccessTokenConverter accessTokenConverter = new MyOIDCJwtAccessTokenConverter(publicJsonWebKey);
//            System.out.println("Key:\n" + accessTokenConverter.getKey());

        MyOIDCAccessTokenConverter tokenConverter = new MyOIDCAccessTokenConverter();
        MyOIDCUserAuthenticationConverter userTokenConverter = new MyOIDCUserAuthenticationConverter();
        userTokenConverter.setUserDetailsService(this.userDetailService);
        tokenConverter.setUserTokenConverter(userTokenConverter);
        accessTokenConverter.setAccessTokenConverter(tokenConverter);

        return accessTokenConverter;
    }


    /**
     * Only  use one key
     *
     * @return RsaJsonWebKey
     * @throws Exception e
     * @since 1.1.0
     */
    @Bean
    public RsaJsonWebKey publicJsonWebKey() throws Exception {
        JsonWebKeySet jsonWebKeySet = jsonWebKeySet();
        return (RsaJsonWebKey) jsonWebKeySet.findJsonWebKey(DEFAULT_KEY_ID, RsaKeyUtil.RSA, USE_SIG, OIDC_ALG);
    }
}
