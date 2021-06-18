package cn.jongwong.oauth.config.sms;

import cn.jongwong.oauth.handler.MyAuthenticationFailureHandler;
import cn.jongwong.oauth.handler.MyAuthenticationSucessHandler;
import cn.jongwong.oauth.validate.code.ValidateCodeProcessor;
import cn.jongwong.oauth.validate.code.ValidateCodeProcessorHolder;
import cn.jongwong.oauth.validate.code.ValidateCodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class SmsCodeAuthenticationFilter extends OncePerRequestFilter {


    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;
    @Autowired
    private MyAuthenticationSucessHandler myAuthenticationSucessHandler;
    @Autowired
    private ValidateCodeProcessorHolder validateCodeProcessorHolder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*验证是/authentication/mobile接口 ，并且是post请求*/
        if (request.getRequestURI().equals("/authentication/mobile")
                && request.getMethod().equalsIgnoreCase("post")) {
            try {


                String mobile = request.getParameter("mobile");
                String code = request.getParameter("code");
                String codeId = request.getParameter("codeId");

                ValidateCodeProcessor validateCodeProcessor = validateCodeProcessorHolder.findValidateCodeProcessor(ValidateCodeType.SMS);

                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("mobile", mobile);
                parameters.put("code", code);
                parameters.put("codeId", codeId);


                Boolean valid = validateCodeProcessor.validate(parameters);
            } catch (AuthenticationException exception) {
                myAuthenticationFailureHandler.onAuthenticationFailure(request, response, exception);
                /*验证码校验失败直接返回，不再执行过滤器链*/
                return;
            }
        }
        /*继续执行过滤器链*/
        filterChain.doFilter(request, response);
    }
}