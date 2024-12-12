package cn.jongwong.server.controller;

import cn.jongwong.server.dto.authentication.AuthenticationSmsSendRequest;
import cn.jongwong.server.dto.authentication.AuthenticationSmsSendResponse;
import cn.jongwong.server.service.SmsVerificationService;
import cn.jongwong.server.util.response.ResponseResult;
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
    public Mono<ResponseResult<AuthenticationSmsSendResponse>> sendSms(@RequestBody AuthenticationSmsSendRequest body) {

        return smsVerificationService.sendVerificationCode(body.getMobile())
                .flatMap(re -> {
                    return Mono.just(ResponseResult.success(re));
                })
                .onErrorResume(ex -> {
                    // 捕获并处理异常，返回错误响应
                    return Mono.just(ResponseResult.error(ex.getMessage()));
                });
    }


}
