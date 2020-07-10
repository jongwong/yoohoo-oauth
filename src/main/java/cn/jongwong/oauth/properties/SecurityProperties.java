
package cn.jongwong.oauth.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "jongwong.security")
@Component
public class SecurityProperties {


    private ValidateCodeProperties code = new ValidateCodeProperties();


    public ValidateCodeProperties getCode() {
        return code;
    }

    public void setCode(ValidateCodeProperties code) {
        this.code = code;
    }

}

