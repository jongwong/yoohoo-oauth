package cn.jongwong.oauth.controller;


import cn.jongwong.oauth.entity.SmsRequestBody;
import cn.jongwong.oauth.entity.User;
import cn.jongwong.oauth.properties.SecurityConstants;
import cn.jongwong.oauth.service.SmsService;
import cn.jongwong.oauth.service.UserService;
import cn.jongwong.oauth.validate.code.ValidateCode;
import cn.jongwong.oauth.validate.code.ValidateCodeProcessor;
import cn.jongwong.oauth.validate.code.ValidateCodeProcessorHolder;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;
import org.jose4j.keys.resolvers.VerificationKeyResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Map;

@RestController
public class BrowserSecurityController {
    private RequestCache requestCache = new HttpSessionRequestCache();

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private UserService userService;
    @GetMapping("/authentication/require")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String requireAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
 /*           if (StringUtils.endsWithIgnoreCase(targetUrl, ".html") || StringUtils.contains(targetUrl, "/login")) {
                redirectStrategy.sendRedirect(request, response, "/login.html");
            }*/
            redirectStrategy.sendRedirect(request, response, "/login.html");

        }
        return "访问的资源需要身份认证！";
    }


    @Autowired
    private ValidateCodeProcessorHolder validateCodeProcessorHolder;


    @Autowired
    private SmsService smsService;

    @GetMapping("/code/sms")
    public ValidateCode createCode(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);
        ValidateCode code = smsService.sendMessage(servletWebRequest);
        return code;
    }

    @PostMapping("/code/test")
    public String test(HttpServletRequest request, HttpServletResponse response, @RequestBody SmsRequestBody body) {
        System.out.println(body);
        ValidateCodeProcessor validateCodeProcessor = validateCodeProcessorHolder.findValidateCodeProcessor("sms");
        return "5555";
    }

    @GetMapping("/code/test1")
    public User test1(HttpServletRequest request, HttpServletResponse response, @RequestBody SmsRequestBody body) {
        User user = userService.getUserByPhoneNumber("18060601823");
        return user;
    }


    @Autowired
    private JsonWebKeySet jsonWebKeySet;

    @GetMapping(value = "/public/jwks", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String jwks() throws Exception {

        return jsonWebKeySet.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY);
    }

    @GetMapping("/verify")
    public Map<String, Object> verify(@PathParam("token") String token, @PathParam("jwks_url") String jwks_url) {
        VerificationKeyResolver verificationKeyResolver = new HttpsJwksVerificationKeyResolver(new HttpsJwks(jwks_url));
        JwtConsumer consumer = new JwtConsumerBuilder()
                .setVerificationKeyResolver(verificationKeyResolver)
                //此处有许可项可配置进行校验，请根据实际需要配置
                //更多帮助可访问 https://bitbucket.org/b_c/jose4j/wiki/JWT%20Examples
                .setRequireExpirationTime()
                .setRequireSubject()
                .setRequireIssuedAt()
//                .setExpectedAudience("myoidc-resource")
//                .setRequireNotBefore()
//                .setRequireJwtId()
                .build();
        try {
            JwtClaims claims = consumer.processToClaims(token);
            return claims.getClaimsMap();
        } catch (InvalidJwtException e) {
            return ImmutableMap.of("error", "verify_failed", "error_details", e.getErrorDetails());
        }
    }

}
