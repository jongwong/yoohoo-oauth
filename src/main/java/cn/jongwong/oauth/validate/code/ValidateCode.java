package cn.jongwong.oauth.validate.code;


import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;


public class ValidateCode implements Serializable {
    private static final long serialVersionUID = -339516035496531943L;
    private String codeId;
    private String code;

    private Boolean expried;

    private long expireIn;
    private LocalDateTime expireTime;

    public ValidateCode() {

    }

    public ValidateCode(String code, long expireIn) {
        this.codeId = UUID.randomUUID().toString();
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
        this.expried = true;
        this.expireIn = expireIn;
    }

    public ValidateCode(String code, LocalDateTime expireTime) {
        Duration duration = Duration.between(LocalDateTime.now(), expireTime);
        this.codeId = UUID.randomUUID().toString();
        this.code = code;
        this.expireTime = expireTime;
        this.expried = true;
        this.expireIn = duration.getSeconds();
    }

    public boolean isExpried() {
        return LocalDateTime.now().isAfter(expireTime);
    }


    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getExpried() {
        return expried;
    }

    public void setExpried(Boolean expried) {
        this.expried = expried;
    }

    public long getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(long expireIn) {
        this.expireIn = expireIn;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
}
