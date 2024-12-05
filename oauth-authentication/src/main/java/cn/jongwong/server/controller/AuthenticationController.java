package cn.jongwong.server.controller;

import cn.jongwong.server.dto.authentication.AuthenticationSmsSendRequest;
import cn.jongwong.server.dto.authentication.AuthenticationSmsSendResponse;
import cn.jongwong.server.service.SmsVerificationService;
import cn.jongwong.server.util.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    @Autowired
    private SmsVerificationService smsVerificationService; // 服务验证短信验证码

    // 发送验证码接口
    @PostMapping("/form/sms/send")
    public Mono<ResponseResult<AuthenticationSmsSendResponse>> sendSms(@RequestBody AuthenticationSmsSendRequest body) {

        return smsVerificationService.sendVerificationCode(body.getPhone())
                .flatMap(re -> {
                    return Mono.just(ResponseResult.success(re));
                })
                .onErrorResume(ex -> {
                    // 捕获并处理异常，返回错误响应
                    return Mono.just(ResponseResult.error(ex.getMessage()));
                });
    }


    @GetMapping("/hello")
    public Mono<String> hello() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {

                    Authentication authentication = securityContext.getAuthentication();
                    if (authentication != null && authentication.isAuthenticated()) {
                        System.out.println("User is authenticated: " + authentication.getName());
                        return "hello";
                    } else {
                        System.out.println("User is not authenticated.");
                        return "redirect:/login";
                    }
                });
    }

    // 验证验证码接口
//    @PostMapping("/sms/verify")
//    public Mono<ResponseResult<Void>> verifySms(@RequestBody AuthenticationSmsVerifyDto body) {
//        return smsVerificationService.verifyCode(body.getPhone(), body.getCode())
//                .flatMap(isValid -> {
//                    if (isValid) {
//                        // 验证成功，返回授权码（这里假设使用固定授权码，实际应用中应生成动态授权码）
//                        return Mono.just(ResponseResult.success());
//                    } else {
//                        return Mono.just(ResponseResult.error("Invalid verification code"));
//                    }
//                });
//    }
}
