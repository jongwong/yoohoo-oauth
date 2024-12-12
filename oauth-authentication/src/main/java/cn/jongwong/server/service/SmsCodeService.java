package cn.jongwong.server.service;

import cn.jongwong.server.enums.SmsCodeTypeEnum;
import reactor.core.publisher.Mono;

public interface SmsCodeService {

    // 发送验证码
    Mono<Boolean> sendCode(String mobileNumber, SmsCodeTypeEnum smsType, long expiryTime);

    // 验证验证码
    Mono<Boolean> verifyCode(String mobileNumber, String code, SmsCodeTypeEnum codeType);
}
