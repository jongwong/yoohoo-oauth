package cn.jongwong.server.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "订单商品明细")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductItemDTO {
    @Schema(description = "商品ID")
    private String id;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品编码")
    private Integer code;

    @Schema(description = "团购商品Id")
    private String groupProductId;

    @Schema(description = "商品图片")
    private String thumbnailImage;

    @Schema(description = "商品价格")
    private Integer price;

    @Schema(description = "商品数量")
    private Integer count;

    @Schema(description = "skuId")
    private String skuId;

    @Schema(description = "sku名称")
    private String skuName;
}
