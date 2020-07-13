package cn.jongwong.oauth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;


@Data
@TableName(value = "tb_secret")
public class Secret implements Serializable {
    private static final long serialVersionUID = -339516038496531943L;
    @TableId(value = "id")
    private int id;
    @TableField(value = "secret_id")
    private String secretId;
    @TableField(value = "secret_key")
    private String secretKey;
    @TableField(value = "secret_name")
    private String secretName;
}
