package cn.jongwong.oauth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;


@Data
@TableName(value = "user")
public class User implements Serializable {
    private static final long serialVersionUID = -339516038496531943L;
    @TableId(value = "id")
    private String id;
    @TableField(value = "username", exist = true)
    private String username;
    @TableField(value = "password")
    private String password;

    @TableField(value = "mobile_phone")
    private String mobilePhone;

    @TableField(value = "e_mail")
    private String eMail;

    @TableField(value = "avatar", exist = true)
    private String avatar;


    private String expired;


    private String locked;


    private String enabled;
}
