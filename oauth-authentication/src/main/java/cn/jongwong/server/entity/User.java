package cn.jongwong.server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@Table("tb_user") // 表名为 user
public class User implements Serializable {
    private static final long serialVersionUID = -339516038496531943L;

    @Id
    private String id;

    @Column(value = "username")
    private String username;

    @Column(value = "password")
    private String password;

    @Column(value = "mobile_phone")
    private String mobilePhone;

    @Column(value = "e_mail")
    private String eMail;

    @Column(value = "avatar")
    private String avatar;

    private int expired;

    private int locked;

    private int enabled;
}
