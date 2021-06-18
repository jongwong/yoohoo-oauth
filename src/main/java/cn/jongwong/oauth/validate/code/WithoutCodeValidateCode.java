package cn.jongwong.oauth.validate.code;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WithoutCodeValidateCode {
    private static final long serialVersionUID = -339516038496531943L;
    private String codeId;

    private Boolean expried;
    private long expireIn;
    private LocalDateTime expireTime;


    public WithoutCodeValidateCode(ValidateCode validateCode) {
        codeId = validateCode.getCodeId();
        expried = validateCode.getExpried();
        expireIn = validateCode.getExpireIn();
        expireTime = validateCode.getExpireTime();
    }
}
