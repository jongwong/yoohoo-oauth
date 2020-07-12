/**
 *
 */
package cn.jongwong.oauth.validate.code;

import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

/**
 * 校验码处理器，封装不同校验码的处理逻辑
 *
 * @author zhailiang
 *
 */
public interface ValidateCodeProcessor {

    /**
     * 验证码放入session时的前缀
     */
    String SESSION_KEY_PREFIX = "SESSION_KEY_FOR_CODE_";

    /**
     * 创建校验码
     *
     * @param request
     * @throws Exception
     */
    ValidateCode create(ServletWebRequest request) throws Exception;

    /**
     * 校验验证码
     *
     * @param servletWebRequest
     * @throws Exception
     */
    Boolean validate(Map<String, String> params);

}
