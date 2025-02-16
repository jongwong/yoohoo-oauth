package cn.jongwong.server.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSubmitDTO {


    @Schema(description = "配送费")
    private String openid;

    @Schema(description = "配送费")
    private BigDecimal amountDelivery;

    @Schema(description = "优惠金额")
    private BigDecimal amountDiscount;

    @Schema(description = "商品金额")
    private BigDecimal amountProduct;

    @Schema(description = "商品列表")
    private List<OrderProductItemDTO> products;

    @Schema(description = "优惠券ID")
    private String couponsId;

    @Schema(description = "收货人姓名")
    private String consigneeName;

    @Schema(description = "收货人手机")
    private String consigneeMobile;

    @Schema(description = "配送点ID")
    private String deliveryPointId;

    @Schema(description = "配送点名称")
    private String deliveryPointName;

    @Schema(description = "配送点地址")
    private String deliveryPointAddress;

    @Schema(description = "订单总金额")
    private BigDecimal amountTotal;

    @Schema(description = "订单状态")
    private Integer status; // 订单状态（10: 待支付, 20: 待收货, 30: 退款中, 40: 已取消, 50: 待评价, 60: 已完成）
}
