package cn.jongwong.oauth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_user") // 表名为 user
public class User implements Serializable {
    private static final long serialVersionUID = -339516038496531943L;

    @TableId(value = "id")
    private String id;

    @TableField(value = "username")
    private String username;

    @TableField(value = "password")
    private String password;

    @TableField(value = "mobile_phone")
    private String mobilePhone;

    @TableField(value = "e_mail")
    private String eMail;

    @TableField(value = "avatar")
    private String avatar;

    private int expired;

    private int locked;

    private int enabled;
}
