package cn.jongwong.oauth.service;

import cn.jongwong.oauth.validate.code.ValidateCode;
import org.springframework.web.context.request.ServletWebRequest;

public interface SmsService {
    ValidateCode sendMessage(ServletWebRequest servletWebRequest) throws Exception;
}
