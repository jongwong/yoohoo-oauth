package cn.jongwong.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "tb_order")
public class OrderVO {

    @Schema(description = "订单ID")
    @Id
    private String id;

    @Schema(description = "订单ID")
    private String num;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "订单总金额")
    private BigDecimal amountTotal;

    @Schema(description = "配送费金额")
    private BigDecimal amountDelivery;

    @Schema(description = "优惠金额")
    private BigDecimal amountDiscount;

    @Schema(description = "商品金额")
    private BigDecimal amountProduct;

    @Schema(description = "优惠券ID")
    private String couponsId;

    @Schema(description = "优惠券名称")
    private String couponsName;


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

    @Schema(description = "订单创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "订单更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "支付状态")
    private Integer paymentStatus;



    @Schema(description = "创建人ID")
    private String createdBy;

    @Schema(description = "创建人名称")
    private String createdByName;

    @Schema(description = "更新人ID")
    private String updatedBy;

    @Schema(description = "更新人名称")
    private String updatedByName;

    @Schema(description = "订单状态")
    private Integer status;  // 订单状态（10: 待支付, 20: 待收货, 30: 退款中, 40: 已取消, 50: 待评价, 60: 已完成）

    @Transient
    @Schema(description = "订单明细")
    private List<OrderItemVO> items;

    @Schema(description = "支付时间")
    private LocalDateTime paymentAt;

    @Transient
    @Schema(description = "预支付ID")
    private Map<String, String> prepayInfo;
}
