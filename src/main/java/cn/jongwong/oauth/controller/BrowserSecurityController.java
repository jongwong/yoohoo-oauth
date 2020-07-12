package cn.jongwong.oauth.controller;


import cn.jongwong.oauth.entity.SmsRequestBody;
import cn.jongwong.oauth.properties.SecurityConstants;
import cn.jongwong.oauth.service.SmsService;
import cn.jongwong.oauth.validate.code.ValidateCode;
import cn.jongwong.oauth.validate.code.ValidateCodeProcessor;
import cn.jongwong.oauth.validate.code.ValidateCodeProcessorHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class BrowserSecurityController {
    private RequestCache requestCache = new HttpSessionRequestCache();

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

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

    ;

}
