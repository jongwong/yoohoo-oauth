package cn.jongwong.oauth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Data
@TableName(value = "user")
public class User implements Serializable {
    private static final long serialVersionUID = -339516038496531943L;
    @TableId(value = "id")
    private String id;
    @TableField(value = "username", exist = true)
    private String username;
    @TableField(value = "avatar", exist = true)
    private String avatar;
    @TableField(value = "expired")
    private String expired;
    @TableField(value = "locked")
    private String locked;
    @TableField(value = "enabled")
    private String enabled;
}
