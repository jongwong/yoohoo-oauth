package cn.jongwong.oauth.config.token;

import cn.jongwong.oauth.service.UserDetailService;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.keys.RsaKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.PrivateKey;

@Configuration
public class JwtTokenConfig {

    @Autowired
    private UserDetailService userDetailsService;

    @Bean
    public TokenStore jwtTokenStore() throws Exception {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JsonWebKeySet jsonWebKeySet() throws Exception {
        //加载 keystore配置文件
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("jwks.json")) {
            String keyJson = CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8));
            return new JsonWebKeySet(keyJson);
        }
    }

    @Bean
    public RsaJsonWebKey publicJsonWebKey() throws Exception {
        JsonWebKeySet jsonWebKeySet = jsonWebKeySet();
        String DEFAULT_KEY_ID = "yoohoo-key-id";
        String USE_SIG = "sig";
        String OIDC_ALG = AlgorithmIdentifiers.RSA_USING_SHA256;
        return (RsaJsonWebKey) jsonWebKeySet.findJsonWebKey(DEFAULT_KEY_ID, RsaKeyUtil.RSA, USE_SIG, OIDC_ALG);
    }

    /**
     * token生成处理：指定签名
     */


    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() throws Exception {


        CustomJwtAccessTokenConverter converter = new CustomJwtAccessTokenConverter();


        PublicJsonWebKey publicJsonWebKey = publicJsonWebKey();
        PrivateKey privateKey = publicJsonWebKey.getPrivateKey();
        KeyPair keyPair = new KeyPair(publicJsonWebKey.getPublicKey(), privateKey);
        converter.setKeyPair(keyPair);

        CustomAccessTokenConverter tokenConverter = new CustomAccessTokenConverter();

        CustomUserAuthenticationConverter userTokenConverter = new CustomUserAuthenticationConverter();
        userTokenConverter.setUserDetailsService(this.userDetailsService);
        tokenConverter.setUserTokenConverter(userTokenConverter);
        /*
         */
        converter.setAccessTokenConverter(tokenConverter);
        return converter;
    }

    @Bean
    public TokenEnhancer jwtTokenEnhancer() {
        return new JwtTokenEnhancer();
    }


}
