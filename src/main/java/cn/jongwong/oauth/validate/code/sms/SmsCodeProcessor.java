/**
 *
 */
package cn.jongwong.oauth.validate.code.sms;


import cn.jongwong.oauth.common.exception.CustomException;
import cn.jongwong.oauth.properties.SecurityConstants;
import cn.jongwong.oauth.validate.code.ValidateCode;
import cn.jongwong.oauth.validate.code.ValidateCodeException;
import cn.jongwong.oauth.validate.code.ValidateCodeType;
import cn.jongwong.oauth.validate.code.impl.AbstractValidateCodeProcessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component("smsValidateCodeProcessor")
public class SmsCodeProcessor extends AbstractValidateCodeProcessor<ValidateCode> {

    /**
     * 短信验证码发送器
     */
    @Autowired
    private SmsCodeSender smsCodeSender;

    public SmsCodeProcessor(RedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected void save(ServletWebRequest request, ValidateCode validateCode) throws ServletRequestBindingException {
        String type = getValidateCodeType(request).toString().toLowerCase();
        String mobile = ServletRequestUtils.getStringParameter(request.getRequest(),
                "mobile");
        String key = "123456";
        redisTemplate.opsForValue().set(key, serializationStrategy.serialize(validateCode), 5, TimeUnit.MINUTES);
    }

    @Override
    protected void send(ServletWebRequest request, ValidateCode validateCode) throws Exception {
        String paramName = SecurityConstants.DEFAULT_PARAMETER_NAME_MOBILE;
        String mobile;
        try {
            mobile = ServletRequestUtils.getStringParameter(request.getRequest(),
                    paramName);
        } catch (ServletRequestBindingException e) {
            throw new CustomException("获取手机号失败");
        }
        if (mobile == null) {
            throw new CustomException("缺失参数mobile");
        }
        if (StringUtils.isBlank(mobile)) {
            throw new CustomException("手机号不能为空");
        }
        smsCodeSender.send(mobile, validateCode.getCode());
    }

    @Override
    public Boolean validate(Map<String, String> parameters) {

        String type = getValidateCodeType().toString().toLowerCase();



        String code;
        String mobile;
        String id;
        mobile = parameters.getOrDefault("mobile", "");

        code = parameters.getOrDefault("code", "");

        String key = "123456";


        if (StringUtils.isBlank(code)) {
            throw new ValidateCodeException("验证码的值不能为空");
        }

        if (code == null) {
            throw new ValidateCodeException("验证码不存在");
        }
        try {


            ValidateCode validateCode =
                    serializationStrategy.deserialize(
                            (byte[]) redisTemplate.opsForValue().get(key), ValidateCode.class);


            if (validateCode.isExpried()) {
                throw new ValidateCodeException("验证码已过期");
            }

            if (!StringUtils.equals(validateCode.getCode(), code)) {
                throw new ValidateCodeException("验证码不匹配");
            }

            redisTemplate.delete(key);
            return true;
        } catch (Exception e) {
            throw new ValidateCodeException("验证码已过期");
        }

    }

}
