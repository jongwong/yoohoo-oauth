package cn.jongwong.server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
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

    private String[] authorities; // e.g., "ROLE_USER,ROLE_ADMIN"

    private LocalDateTime lastLoginAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
