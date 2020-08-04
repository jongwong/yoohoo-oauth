package cn.jongwong.oauth.config;

import ch.qos.logback.core.rolling.helper.TokenConverter;

import com.google.common.collect.ImmutableMap;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.*;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;


import org.springframework.util.Assert;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;


import org.springframework.security.oauth2.common.util.JsonParserFactory;


public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {
    private AccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();


    private final JsonParser jsonParser = JsonParserFactory.create();

    private JsonParser objectMapper = JsonParserFactory.create();
    private String verifierKey = (new RandomValueStringGenerator()).generate();
    private Signer signer;
    private String signingKey;

    @Autowired
    RsaJsonWebKey publicJsonWebKey;

    private SignatureVerifier verifier;

    CustomJwtAccessTokenConverter() {
        this.signer = new MacSigner(this.verifierKey);
        this.signingKey = this.verifierKey;
    }


    @Override
    public void setKeyPair(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        Assert.state(privateKey instanceof RSAPrivateKey, "KeyPair must be an RSA ");
        this.signer = new RsaSigner((RSAPrivateKey) privateKey);
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        this.verifier = new RsaVerifier(publicKey);
        this.verifierKey = "-----BEGIN PUBLIC KEY-----\n" + new String(Base64.encode(publicKey.getEncoded())) + "\n-----END PUBLIC KEY-----";
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        OAuth2AccessToken enhanceToken = super.enhance(accessToken, authentication);

        //if have openid, add id_token
        if (authentication.getOAuth2Request().getScope().contains("openid")) {
            ImmutableMap<String, String> extHeader = ImmutableMap.of(
                    "kid", publicJsonWebKey.getKeyId());
            String idToken = "";
            idToken = encodeWithHeader(accessToken, authentication, extHeader);
            enhanceToken.getAdditionalInformation().put("id_token", idToken);

        }

        return enhanceToken;
    }


    @Override
    public String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String content;
        try {
            content = this.objectMapper.formatMap(this.tokenConverter.convertAccessToken(accessToken, authentication));
        } catch (Exception var5) {
            throw new IllegalStateException("Cannot convert access token to JSON", var5);
        }

        String token = JwtHelper.encode(content, this.signer).getEncoded();
        return token;
    }


    private String encodeWithHeader(OAuth2AccessToken accessToken, OAuth2Authentication authentication, ImmutableMap<String, String> extHeader) {
        String content;
        try {
            content = objectMapper.formatMap(getAccessTokenConverter().convertAccessToken(accessToken, authentication));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot convert access token to JSON", e);
        }

        return JwtHelper.encode(content, signer, extHeader).getEncoded();
    }

//    @Override
//    protected Map<String, Object> decode(String token) {
//        Map<String, String> headers = this.jwtHeaderConverter.convert(token);
//        String keyIdHeader = (String)headers.get("kid");
//        if (keyIdHeader == null) {
//            throw new InvalidTokenException("Invalid JWT/JWS: kid is a required JOSE Header");
//        } else {
//            JwkDefinitionSource.JwkDefinitionHolder jwkDefinitionHolder = this.jwkDefinitionSource.getDefinitionLoadIfNecessary(keyIdHeader);
//            if (jwkDefinitionHolder == null) {
//                throw new InvalidTokenException("Invalid JOSE Header kid (" + keyIdHeader + ")");
//            } else {
//                JwkDefinition jwkDefinition = jwkDefinitionHolder.getJwkDefinition();
//                String algorithmHeader = (String)headers.get("alg");
//                if (algorithmHeader == null) {
//                    throw new InvalidTokenException("Invalid JWT/JWS: alg is a required JOSE Header");
//                } else if (jwkDefinition.getAlgorithm() != null && !algorithmHeader.equals(jwkDefinition.getAlgorithm().headerParamValue())) {
//                    throw new InvalidTokenException("Invalid JOSE Header alg (" + algorithmHeader + ")" + " does not match algorithm associated to JWK with " + "kid" + " (" + keyIdHeader + ")");
//                } else {
//                    SignatureVerifier verifier = jwkDefinitionHolder.getSignatureVerifier();
//                    Jwt jwt = JwtHelper.decode(token);
//                    jwt.verifySignature(verifier);
//                    Map<String, Object> claims = this.jsonParser.parseMap(jwt.getClaims());
//                    if (claims.containsKey("exp") && claims.get("exp") instanceof Integer) {
//                        Integer expiryInt = (Integer)claims.get("exp");
//                        claims.put("exp", new Long((long)expiryInt));
//                    }
//
//                    this.getJwtClaimsSetVerifier().verify(claims);
//                    return claims;
//                }
//
//
//
//            }
//        }
//
//
////        SignatureVerifier verifier = jwkDefinitionHolder.getSignatureVerifier();
////        Jwt jwt = JwtHelper.decode(token);
////        jwt.verifySignature(verifier);
////        Map<String, Object> claims = this.jsonParser.parseMap(jwt.getClaims());
////        if (claims.containsKey("exp") && claims.get("exp") instanceof Integer) {
////            Integer expiryInt = (Integer)claims.get("exp");
////            claims.put("exp", new Long((long)expiryInt));
////        }
////
////        this.getJwtClaimsSetVerifier().verify(claims);
////        return claims;
//    }
}
