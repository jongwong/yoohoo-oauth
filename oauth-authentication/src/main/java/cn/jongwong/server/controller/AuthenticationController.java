package cn.jongwong.server.controller;

import cn.jongwong.server.dto.authentication.AuthenticationSmsSendDTO;
import cn.jongwong.server.dto.authentication.AuthenticationSmsSendRO;
import cn.jongwong.server.service.SmsVerificationService;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    @Autowired
    private SmsVerificationService smsVerificationService; // 服务验证短信验证码
    @Autowired
    private ReactiveAuthenticationManager authenticationManager;

    // 发送验证码接口
    @PostMapping("/form/sms/send")
    public Mono<Response<AuthenticationSmsSendRO>> sendSms(@RequestBody AuthenticationSmsSendDTO body) {

        return smsVerificationService.sendVerificationCode(body.getMobile())
                .flatMap(re -> {
                    return Mono.just(Response.success(re));
                })
                .onErrorResume(ex -> {
                    // 捕获并处理异常，返回错误响应
                    return Mono.just(Response.error(ex.getMessage()));
                });
    }


}
