package cn.jongwong.oauth.oidc;


import org.jose4j.jwt.ReservedClaimNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import java.util.HashMap;
import java.util.Map;


public class MyOIDCUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    private static final Logger LOG = LoggerFactory.getLogger(MyOIDCUserAuthenticationConverter.class);

    public MyOIDCUserAuthenticationConverter() {
    }

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> myOIDCMap = new HashMap<>();
        Map<String, ?> oldMap = super.convertUserAuthentication(authentication);
        myOIDCMap.putAll(oldMap);

        //按协议规范增加 required 属性
        // https://openid.net/specs/openid-connect-core-1_0.html#IDToken
        myOIDCMap.put(ReservedClaimNames.ISSUER, "http://localhost:8080");
        myOIDCMap.put(ReservedClaimNames.ISSUED_AT, System.currentTimeMillis() / 1000);

        Object details = authentication.getDetails();
        if (details instanceof UserDetails) {
            //改过
            myOIDCMap.put(ReservedClaimNames.SUBJECT, ((UserDetails) details).getUsername());
        } else {
            myOIDCMap.put(ReservedClaimNames.SUBJECT, myOIDCMap.get(USERNAME));
        }
        return myOIDCMap;
    }
}
