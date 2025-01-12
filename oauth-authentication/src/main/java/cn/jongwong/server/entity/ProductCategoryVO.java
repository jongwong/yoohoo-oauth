package cn.jongwong.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "tb_product_category")
public class ProductCategoryVO {

    @Id
    @Schema(description = "类别ID")
    private String id;

    @Schema(description = "类别代码")
    private Integer code;

    @Schema(description = "类别名称")
    private String name;

    @Schema(description = "类别描述")
    private String description;

    @Schema(description = "是否启用")
    private Integer enable;

    @Schema(description = "父级类别ID，根类别为null")
    private String parentId;

    @Schema(description = "类别层级 (1-4)")
    private Integer level;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "创建人ID")
    private String createdBy;

    @Schema(description = "创建人名称")
    private String createdByName;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "更新人ID")
    private String updatedBy;

    @Schema(description = "更新人名称")
    private String updatedByName;
}
