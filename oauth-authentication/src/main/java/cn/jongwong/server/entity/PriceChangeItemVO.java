package cn.jongwong.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "tb_price_change_item")
public class PriceChangeItemVO {

    @Id
    @Schema(description = "变价记录 ID")
    private String id;


    @Schema(description = "商品 ID")
    private String productId;

    @Schema(description = "SKU ID（如果没有 SKU，则为 NULL）")
    private String skuId;

    @Schema(description = "变价后价格 (单位：分)")
    private Integer price;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "商品名称")
    @Transient
    private String productName;

    @Schema(description = "SKU 名称")
    @Transient
    private String skuName;

    @Schema(description = "商品图片")
    @Transient
    private String imageUrl;

    @Schema(description = "原价 (单位：分)")
    @Transient
    private Integer originalPrice;

    @Schema(description = "市场价 (单位：分)")
    @Transient
    private Integer marketPrice;

}
