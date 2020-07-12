package cn.jongwong.oauth.service.impl;

import cn.jongwong.oauth.entity.User;
import cn.jongwong.oauth.service.SmsService;
import cn.jongwong.oauth.validate.code.ValidateCode;
import cn.jongwong.oauth.validate.code.ValidateCodeProcessorHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletWebRequest;


@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private ValidateCodeProcessorHolder validateCodeProcessorHolder;

    @Override
    public ValidateCode sendMessage(ServletWebRequest servletWebRequest) throws Exception {
        ValidateCode code = validateCodeProcessorHolder.findValidateCodeProcessor("sms").create(servletWebRequest);

        return code;
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        return null;
    }
}
