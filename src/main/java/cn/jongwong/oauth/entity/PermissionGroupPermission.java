package cn.jongwong.oauth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_permission_group_permission")
public class PermissionGroupPermission implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("permission_group_id")
    private String permissionGroupId;

    @TableField("permission_id")
    private String permissionId;
}
