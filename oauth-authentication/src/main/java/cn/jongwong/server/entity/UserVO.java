package cn.jongwong.server.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("tb_user") // 表名为 tb_user
public class UserVO implements Serializable {
    private static final long serialVersionUID = -339516038496531943L;

    @Id
    private String id;

    @Column("username") // 映射数据库字段 'username'
    private String username;

    @Column("password") // 映射数据库字段 'password'
    private String password;

    @Column("mobile") // 映射数据库字段 'mobile'
    private String mobile;

    @Column("email") // 映射数据库字段 'email'
    private String email;

    private String avatar;

    private int expired;

    private int locked;

    private int enabled;

    private String name;

    private String nickname;

    @JsonSerialize(using = StringArraySerializer.class)
    private String authorities; // e.g., "ROLE_USER,ROLE_ADMIN"

    private LocalDateTime lastLoginAt;

    @Schema(description = "创建人ID")
    private String createdBy;

    @Schema(description = "创建人名称")
    private String createdByName;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新人ID")
    private String updatedBy;

    @Schema(description = "更新人名称")
    private String updatedByName;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;


    public String[] getAuthorities() {
        // 将逗号分隔的字符串转换为数组
        return authorities != null ? authorities.split(",") : new String[0];
    }

    public void setAuthorities(String[] tagsArray) {
        // 将数组转换为逗号分隔的字符串存储
        this.authorities = String.join(",", tagsArray);
    }
}
