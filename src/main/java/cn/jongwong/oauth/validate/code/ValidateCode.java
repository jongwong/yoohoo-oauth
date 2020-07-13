/**
 *
 */
package cn.jongwong.oauth.validate.code;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


/**
 * @author zhailiang
 *
 */
public class ValidateCode implements Serializable {
    private static final long serialVersionUID = -339516038496531943L;
    private String id;
    private String code;

    private LocalDateTime expireTime;


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public ValidateCode(String code, int expireIn) {
        this.id = UUID.randomUUID().toString();
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public ValidateCode(String code, LocalDateTime expireTime) {
        this.id = UUID.randomUUID().toString();
        this.code = code;
        this.expireTime = expireTime;
    }

    public boolean isExpried() {
        return LocalDateTime.now().isAfter(expireTime);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

}
