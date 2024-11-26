package cn.jongwong.oauth.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_permission")
public class Permission implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    @TableField("name")
    private String name;

    @TableField("type")
    private String type; // API, BUTTON, MENU

    @TableField("resource")
    private String resource;

    @TableField("created_at")
    private String createdAt;

    @TableField("updated_at")
    private String updatedAt;
}
