package cn.jongwong.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "tb_mini_app_category_menu")
public class MiniAppMenuVO {
    @Id
    @Schema(description = "菜单项的唯一标识")
    private String id;

    @Schema(description = "菜单名称")
    private String title;

    @Schema(description = "类别ID，外键关联到商品类别")
    private String categoryId;

    @Schema(description = "排序字段，用于菜单项的显示顺序")
    private Integer sort;
}
