package cn.jongwong.server.service.impl;

import cn.jongwong.server.enums.SmsCodeTypeEnum;
import cn.jongwong.server.service.SmsCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class SmsCodeServiceImpl implements SmsCodeService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;  // Redis操作模板


    // 模拟发送验证码
    @Override
    public Mono<Boolean> sendCode(String mobileNumber, SmsCodeTypeEnum smsType, long expiryTime) {
        return Mono.defer(() -> {
            if (mobileNumber == null || mobileNumber.isEmpty()) {
                return Mono.error(new IllegalArgumentException("Mobile number cannot be null or empty"));
            }
            try {
                // Redis中的验证码存储键
                String redisKey = generateSmsCodeRedisKey(mobileNumber, smsType);

                // 获取Redis中的值操作对象
                ValueOperations<String, String> ops = redisTemplate.opsForValue();


                // 检查Redis中是否已经存在验证码
                if (ops.get(redisKey) != null) {
                    // 如果存在并且未过期，更新过期时间，不重新生成验证码
                    System.out.printf("发送的验证码是：%s%n", ops.get(redisKey));
                    ops.set(redisKey, Objects.requireNonNull(ops.get(redisKey)), expiryTime, TimeUnit.MILLISECONDS);  // 更新过期时间为5分钟
                } else {

                    String verificationCode = generateSmsCode();
                    System.out.printf("发送的验证码是：%s%n", verificationCode);
                    // 如果Redis中没有验证码，则生成并存入Redis
                    ops.set(redisKey, verificationCode, expiryTime, TimeUnit.MILLISECONDS);  // 设置过期时间为5分钟
                }

                String msg = generateMessage(mobileNumber, smsType);


                return Mono.just(true);  // 返回成功标志
            } catch (Exception e) {
                return Mono.error(new RuntimeException("Error sending verification code", e));  // 错误暴露
            }
        });
    }


    // 模拟验证验证码
    @Override
    public Mono<Boolean> verifyCode(String mobileNumber, String code, SmsCodeTypeEnum codeType) {

        if (mobileNumber == null || mobileNumber.isEmpty() || code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Mobile number or code cannot be null or empty");
        }
        return Mono.just(true);
//
//        try {
//            // 从Redis中获取验证码
//            String redisKey = generateSmsCodeRedisKey(mobileNumber, codeType);
//            ValueOperations<String, String> ops = redisTemplate.opsForValue();
//            String storedCode = ops.get(redisKey);  // 从Redis获取验证码
//
//            // 校验验证码是否正确
//            if (storedCode != null && storedCode.equals(code)) {
//                return Mono.just(true);  // 验证成功
//            } else {
//                return Mono.just(false);  // 验证失败
//            }
//        } catch (Exception e) {
//            return Mono.just(false);  // 错误暴露
//        }
    }

    private String generateMessage(String verificationCode, SmsCodeTypeEnum smsType) {
        // 根据短信类型生成不同的短信内容
        switch (smsType) {
            case OAUTH_AUTHENTICATION:
                return "Your verification code is " + verificationCode;
            default:
                return "Your code is " + verificationCode;
        }
    }

    private String generateSmsCodeRedisKey(String mobileNumber, SmsCodeTypeEnum smsType) {
        return "sms:" + smsType + ":" + mobileNumber;
    }

    private String generateSmsCode() {
        // 生成6位数字验证码
        return String.format("%06d", (int) (Math.random() * 1000000));
    }


}
