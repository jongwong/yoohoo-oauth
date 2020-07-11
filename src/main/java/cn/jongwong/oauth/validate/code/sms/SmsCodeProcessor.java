/**
 *
 */
package cn.jongwong.oauth.validate.code.sms;


import cn.jongwong.oauth.properties.SecurityConstants;
import cn.jongwong.oauth.validate.code.ValidateCode;
import cn.jongwong.oauth.validate.code.ValidateCodeException;
import cn.jongwong.oauth.validate.code.ValidateCodeType;
import cn.jongwong.oauth.validate.code.impl.AbstractValidateCodeProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;


@Component("smsValidateCodeProcessor")
public class SmsCodeProcessor extends AbstractValidateCodeProcessor<ValidateCode> {

    /**
     * 短信验证码发送器
     */
    @Autowired
    private SmsCodeSender smsCodeSender;

    @Override
    protected void send(ServletWebRequest request, ValidateCode validateCode) throws Exception {
        String paramName = SecurityConstants.DEFAULT_PARAMETER_NAME_MOBILE;
        String mobile = ServletRequestUtils.getRequiredStringParameter(request.getRequest(), paramName);
        smsCodeSender.send(mobile, validateCode.getCode());
    }

    @Override
    public void validate(ServletWebRequest request) {
        System.out.println(request);
        String code;
        String mobile;
        ValidateCodeType processorType = getValidateCodeType(request);
        try {
            code = ServletRequestUtils.getStringParameter(request.getRequest(),
                    processorType.getParamNameOnValidate());
            mobile = ServletRequestUtils.getStringParameter(request.getRequest(),
                    "mobile");
        } catch (ServletRequestBindingException e) {
            throw new ValidateCodeException("获取验证码的值失败");
        }

//        ValidateCodeType processorType = getValidateCodeType(request);
//        String sessionKey = getSessionKey(request);
//
//        C codeInSession = (C) sessionStrategy.getAttribute(request, sessionKey);
//
//        String codeInRequest;
//        try {
//            codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(),
//                    processorType.getParamNameOnValidate());
//        } catch (ServletRequestBindingException e) {
//            throw new ValidateCodeException("获取验证码的值失败");
//        }
//
//        if (StringUtils.isBlank(codeInRequest)) {
//            throw new ValidateCodeException(processorType + "验证码的值不能为空");
//        }
//
//        if (codeInSession == null) {
//            throw new ValidateCodeException(processorType + "验证码不存在");
//        }
//
//        if (codeInSession.isExpried()) {
//            sessionStrategy.removeAttribute(request, sessionKey);
//            throw new ValidateCodeException(processorType + "验证码已过期");
//        }
//
//        if (!StringUtils.equals(codeInSession.getCode(), codeInRequest)) {
//            throw new ValidateCodeException(processorType + "验证码不匹配");
//        }
//
//        sessionStrategy.removeAttribute(request, sessionKey);
    }

}
