package cn.jongwong.server.service.impl;

import cn.jongwong.server.dto.authentication.AuthenticationSmsSendResponse;
import cn.jongwong.server.enums.SmsCodeTypeEnum;
import cn.jongwong.server.service.SmsCodeService;
import cn.jongwong.server.service.SmsVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Service
public class SmsVerificationServiceImpl implements SmsVerificationService {

    @Autowired
    SmsCodeService smsCodeService;

    // 模拟发送验证码
    @Override
    public Mono<AuthenticationSmsSendResponse> sendVerificationCode(String mobileNumber) {
        // 使用Mono.defer确保逻辑在订阅时执行
        return Mono.defer(() -> {
            if (mobileNumber == null || mobileNumber.isEmpty()) {
                return Mono.error(new IllegalArgumentException("Mobile number cannot be null or empty"));
            }
            try {
                int minutes = 2;
                long expiryTimeInSeconds = TimeUnit.MINUTES.toMillis(minutes);
                // 创建SmsSendDataDto对象并设置expiryTime
                AuthenticationSmsSendResponse authenticationSmsSendResponse = new AuthenticationSmsSendResponse();
                authenticationSmsSendResponse.setExpireIn(expiryTimeInSeconds);
                // 调用外部服务并直接返回结果作为 Mono
                return smsCodeService
                        .sendCode(mobileNumber, SmsCodeTypeEnum.OAUTH_AUTHENTICATION, expiryTimeInSeconds)
                        .flatMap(success -> {

                            if (Boolean.TRUE.equals(success)) {

                                return Mono.just(authenticationSmsSendResponse); // 成功发送
                            } else {
                                return Mono.error(new RuntimeException("Failed to send SMS code")); // 失败处理
                            }
                        })
                        .onErrorResume(error -> {
                            return Mono.error(new RuntimeException(error.getMessage())); // 出现异常返回 false
                        });
            } catch (Exception e) {
                return Mono.error(new RuntimeException("Error sending verification code", e));  // 错误暴露
            }
        });
    }

    // 模拟验证验证码
    @Override
    public Mono<Boolean> verifyCode(String mobileNumber, String code) {
        // 使用Mono.defer确保逻辑在订阅时执行
        return Mono.defer(() -> {
            if (mobileNumber == null || mobileNumber.isEmpty()) {
                return Mono.error(new IllegalArgumentException("Mobile number cannot be null or empty"));
            }
//            try {
//                // 调用外部服务并直接返回结果作为 Mono
//                return smsCodeService
//                        .verifyCode(mobileNumber, code, SmsCodeTypeEnum.OAUTH_AUTHENTICATION)
//                        .flatMap(success -> {
//                            if (Boolean.TRUE.equals(success)) {
//                                return Mono.just(true); // 成功发送
//                            } else {
//                                return Mono.error(new RuntimeException("Failed to send SMS code")); // 失败处理
//                            }
//                        })
//                        .onErrorResume(error -> {
//                            return Mono.just(false); // 出现异常返回 false
//                        });
//            } catch (Exception e) {
//                return Mono.error(new RuntimeException("Error sending verification code", e));  // 错误暴露
//            }
            return null;
        });
    }


}
