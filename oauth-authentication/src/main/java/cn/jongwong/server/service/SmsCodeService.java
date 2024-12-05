package cn.jongwong.server.service;

import cn.jongwong.server.enums.SmsCodeTypeEnum;
import reactor.core.publisher.Mono;

public interface SmsCodeService {

    // 发送验证码
    Mono<Boolean> sendCode(String phoneNumber, SmsCodeTypeEnum smsType, long expiryTime);

    // 验证验证码
    Mono<Boolean> verifyCode(String phoneNumber, String code, SmsCodeTypeEnum codeType);
}
