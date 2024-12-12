package cn.jongwong.server.service;

import cn.jongwong.server.dto.authentication.AuthenticationSmsSendResponse;
import reactor.core.publisher.Mono;

public interface SmsVerificationService {

    // 发送验证码
    Mono<AuthenticationSmsSendResponse> sendVerificationCode(String mobileNumber);

    // 验证验证码
    Mono<Boolean> verifyCode(String mobileNumber, String code);
}
