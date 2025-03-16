package cn.jongwong.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Table("tb_product_sku")
public class ProductSkuVO {

    @Schema(description = "SKU ID (主键)")
    @Id
    private String id;

    @Schema(description = "商品 ID")
    @NotNull(message = "商品 ID 不能为空")
    private String productId;

    @Schema(description = "SKU 组合名称")
    @NotNull(message = "SKU 名称不能为空")
    private String name;

    @Schema(description = "SKU 价格 (单位：分)")
    @NotNull(message = "售卖价不能为空")
    private Integer price;

    @Schema(description = "SKU 价格 (单位：分)")
    @NotNull(message = "市场价不能为空")
    private Integer marketPrice;


    @Schema(description = "采购价格 (单位：分)")
    @NotNull(message = "采购价不能为空")
    private Integer purchasePrice;


    @Schema(description = "库存数量")
    private Integer stock;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "sku参数")
    private String skuParameter;
}
