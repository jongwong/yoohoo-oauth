/**
 *
 */
package cn.jongwong.oauth.validate.code;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * @author zhailiang
 *
 */
public interface ValidateCodeGenerator {

    ValidateCode generate(ServletWebRequest request);

}
