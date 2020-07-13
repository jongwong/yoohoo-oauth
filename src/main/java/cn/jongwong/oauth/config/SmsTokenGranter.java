package cn.jongwong.oauth.config;


import cn.jongwong.oauth.entity.User;
import cn.jongwong.oauth.service.UserService;
import cn.jongwong.oauth.validate.code.ValidateCodeProcessor;
import cn.jongwong.oauth.validate.code.ValidateCodeProcessorHolder;
import cn.jongwong.oauth.validate.code.ValidateCodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Collections;
import java.util.Map;


public class SmsTokenGranter extends AbstractTokenGranter {


    private ValidateCodeProcessorHolder validateCodeProcessorHolder;
    private UserService userService;

    private static final String GRANT_TYPE = "authorization_sms";
    private final AuthenticationManager authenticationManager;

    public SmsTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory,
                           ValidateCodeProcessorHolder validateCodeProcessorHolder, UserService userService) {

        this(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE, validateCodeProcessorHolder, userService);
    }

    protected SmsTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType
            , ValidateCodeProcessorHolder validateCodeProcessorHolder, UserService userService) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
        this.validateCodeProcessorHolder = validateCodeProcessorHolder;
        this.userService = userService;
    }

    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = tokenRequest.getRequestParameters();


        String mobile = parameters.getOrDefault("mobile", "");
        String code = parameters.getOrDefault("code", "");
        ValidateCodeProcessor validateCodeProcessor = validateCodeProcessorHolder.findValidateCodeProcessor(ValidateCodeType.SMS);

        Boolean valid = validateCodeProcessor.validate(parameters);

        if (valid) {
            User user = userService.getUserByPhoneNumber(mobile);
            OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(client);
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, Collections.singletonList(new SimpleGrantedAuthority("USER")));
            return new OAuth2Authentication(oAuth2Request, authentication);
        } else {
            throw new AuthenticationServiceException("user not found");
        }


    }
}
