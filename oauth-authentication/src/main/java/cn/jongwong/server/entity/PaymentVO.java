package cn.jongwong.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PaymentVO {

    @Schema(description = "支付记录ID")
    private String id;

    @Schema(description = "订单ID")
    private String orderId;

    @Schema(description = "支付方式 (1: 支付宝, 2: 微信, 3: 信用卡, 4: 其他)")
    private Integer paymentMethod;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "支付状态 (1: 待支付, 2: 已支付, 3: 失败, 4: 退款中, 5: 已退款)")
    private Integer status;

    @Schema(description = "支付时间")
    private LocalDateTime paymentAt;

    @Schema(description = "支付平台返回的支付流水号")
    private String transactionId;

    @Schema(description = "支付备注")
    private String paymentNote;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "创建人ID")
    private String createdBy;

    @Schema(description = "创建人名称")
    private String createdByName;

    @Schema(description = "更新人ID")
    private String updatedBy;

    @Schema(description = "更新人名称")
    private String updatedByName;
}
