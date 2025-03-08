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
@Table(name = "tb_purchase_group_product")
public class ClientPurchaseGroupProductVO {

    @Id
    @Schema(description = "团购商品关联表主键ID")
    private String id;

    @Schema(description = "团购ID")
    private String purchaseGroupId;

    @Schema(description = "团购商品关联ID")
    private String groupProductId;

    @Schema(description = "商品团购最大库存")
    private Integer maxStock;

    @Schema(description = "已售数量")
    private Integer soldQuantity;

    // 商品相关信息
    @Schema(description = "商品编号")
    private String productId;

    // 商品相关信息
    @Schema(description = "商品编号")
    private Integer productCode;

    @Schema(description = "商品名称")
    private String productName;


    @Schema(description = "商品价格")
    private Integer price;

    @Schema(description = "商品类别名称")
    private String categoryName;

    @Schema(description = "商品类别ID")
    private String categoryId;

    @Schema(description = "商品类别ID")
    private Integer categoryCode;

    @Schema(description = "建档状态")
    private Integer archivedStatus;

    @Schema(description = "上架状态")
    private Integer listedStatus;

    @Schema(description = "配送点ID")
    private String distributionPointId;

    @Schema(description = "缩略图")
    private String thumbnailImageUrl;


    @Schema(description = "最小成团人数")
    private Integer groupRequiredCount;

    // 团购相关信息
    @Schema(description = "团购名称")
    private String groupName;


    @Schema(description = "团购状态 (1: 开团中, 2: 开团成功, 3: 已结束)")
    private Integer groupStatus;

    @Schema(description = "团购启用状态 (1: 启用, 0: 停用)")
    private Integer groupEnable;

    @Schema(description = "团购开始时间")
    private LocalDateTime timeGroupStart;

    @Schema(description = "团购结束时间")
    private LocalDateTime timeGroupEnd;

    // 配送相关信息
    @Schema(description = "配送开始时间")
    private LocalDateTime timeDeliveryStart;

    @Schema(description = "配送截止时间")
    private LocalDateTime timeDeliveryEnd;


}
