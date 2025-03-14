package cn.jongwong.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "tb_purchase_group_product")
public class PurchaseGroupProductVO {

    @Id
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "团购ID")
    private String purchaseGroupId;


    @Schema(description = "商品最大库存")
    private Integer maxStock;




    @Schema(description = "商品ID")
    private String productId;

    @Schema(description = "商品编号")
    @Transient
    private Integer productCode;

    @Schema(description = "商品名称")
    @Transient
    private String productName;


    @Schema(description = "商品价格")
    @Transient
    private Integer price;

    @Schema(description = "商品折扣价")
    private Integer discountPrice;


    @Schema(description = "商品图片")
    @Transient
    private String imageUrl;

}
