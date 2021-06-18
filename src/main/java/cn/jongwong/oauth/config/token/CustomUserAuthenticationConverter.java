package cn.jongwong.oauth.config.token;

import cn.jongwong.oauth.service.UserDetailService;
import org.jose4j.jwt.ReservedClaimNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import java.util.HashMap;
import java.util.Map;

public class CustomUserAuthenticationConverter extends DefaultUserAuthenticationConverter {


    public CustomUserAuthenticationConverter() {
    }

    @Value("${openid-server.config.issuer}")
    private String issuer;

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> myOIDCMap = new HashMap<>();
        Map<String, ?> oldMap = super.convertUserAuthentication(authentication);
        myOIDCMap.putAll(oldMap);

        //按协议规范增加 required 属性
        // https://openid.net/specs/openid-connect-core-1_0.html#IDToken
        myOIDCMap.put(ReservedClaimNames.ISSUER, issuer);
        myOIDCMap.put(ReservedClaimNames.ISSUED_AT, System.currentTimeMillis() / 1000);

        Object username = authentication.getName();
        myOIDCMap.put(ReservedClaimNames.SUBJECT, username);


        return myOIDCMap;
    }
}
