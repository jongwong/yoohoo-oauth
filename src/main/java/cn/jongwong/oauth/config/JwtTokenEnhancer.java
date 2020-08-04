package cn.jongwong.oauth.config;

import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwt.ReservedClaimNames;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class JwtTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {

        Map<String, Object> info = new HashMap<>();
        info.put("provider", "Yoohoo");
        OAuth2Request request = oAuth2Authentication.getOAuth2Request();
        Map<String, String> parameters = request.getRequestParameters();
        String client_id = parameters.getOrDefault("client_id", "");
        if (!StringUtils.equals(client_id, "")) {
            info.put(ReservedClaimNames.AUDIENCE, client_id);
        }
        String nonce = parameters.getOrDefault("nonce", "");
        if (!StringUtils.equals(nonce, "")) {
            info.put("nonce", nonce);
        }

        //设置附加信息
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);
        return oAuth2AccessToken;
    }
}
