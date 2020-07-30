package cn.jongwong.oauth.oidc.controller;


import cn.jongwong.oauth.oidc.util.OIDCUtils;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import static cn.jongwong.oauth.oidc.Constants.ID_TOKEN;
import static cn.jongwong.oauth.oidc.Constants.OIDC_ALG;

import static cn.jongwong.oauth.oidc.util.OIDCUtils.SCOPE_OPENID;
import static cn.jongwong.oauth.oidc.util.OIDCUtils.SCOPE_READ;
import static cn.jongwong.oauth.oidc.util.OIDCUtils.SCOPE_WRITE;

/**
 * <p>
 * Discovery API
 * https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata
 */
@RestController
public class DiscoveryEndpoint {


    /**
     * https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfigurationRequest
     *
     * @return view
     * @throws Exception Exception
     */
    @GetMapping("/.well-known/openid-configuration")
    public Map<String, Object> configuration() throws Exception {
        Map<String, Object> model = new HashMap<>();
        String host = "http://localhost:8080";
        model.put("issuer", host);
        model.put("authorization_endpoint", OIDCUtils.authorizeEndpoint(host));
        model.put("token_endpoint", OIDCUtils.tokenEndpoint(host));
        model.put("userinfo_endpoint", OIDCUtils.userinfoEndpoint(host));

        model.put("jwks_uri", OIDCUtils.jwksURI(host));
        model.put("registration_endpoint", OIDCUtils.registrationEndpoint(host));

        model.put("scopes_supported", Arrays.asList(SCOPE_OPENID, SCOPE_READ, SCOPE_WRITE));
        model.put("grant_types_supported", OIDCUtils.GrantType.types());
        model.put("response_types_supported", Arrays.asList("token", "code", ID_TOKEN));
        //ALG:
        model.put("id_token_signing_alg_values_supported", Collections.singletonList(OIDC_ALG));
        // "pairwise",
        model.put("subject_types_supported", Arrays.asList("public"));
        model.put("claims_supported", Arrays.asList("sub", "aud", "scope", "iss", "exp", "iat", "client_id", "authorities", "user_name"));
        return model;
    }

    @Autowired
    private JsonWebKeySet jsonWebKeySet;

    @GetMapping(value = "/public/jwks", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String jwks() throws Exception {
        return jsonWebKeySet.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY);
    }


}
