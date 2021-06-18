/**
 *
 */
package cn.jongwong.oauth.validate.code;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * @author jongwong
 */
public interface ValidateCodeGenerator {

    ValidateCode generate(ServletWebRequest request);

}
