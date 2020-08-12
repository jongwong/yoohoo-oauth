/**
 *
 */
package cn.jongwong.oauth.validate.code;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.codehaus.jackson.map.ext.JodaDeserializers;
import org.codehaus.jackson.map.ext.JodaSerializers;

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

    private Boolean expried;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime expireTime;


    //
    public ValidateCode() {
    }
    public ValidateCode(String code, int expireIn) {
        this.id = UUID.randomUUID().toString();
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
        this.expried = true;
    }

    public ValidateCode(String code, LocalDateTime expireTime) {
        this.id = UUID.randomUUID().toString();
        this.code = code;
        this.expireTime = expireTime;
        this.expried = true;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getExpried() {
        return expried;
    }

    public void setExpried(Boolean expried) {
        this.expried = expried;
    }
}
