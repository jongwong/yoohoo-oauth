package cn.jongwong.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "tb_payment")
public class PaymentVO {

    @Schema(description = "支付记录ID")
    @Id
    private String id;

    @Schema(description = "订单ID")
    private String orderId;

    @Schema(description = "支付方式 (1: 微信支付, 2: 支付宝, 3: 银行卡等)")
    private Integer paymentMethod; // 数据库中为 tinyint

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "支付状态 (10: 支付中, 20: 支付成功, 30: 支付失败, 40: 已取消)")
    private Integer status; // 数据库中为 tinyint

    @Schema(description = "支付时间")
    private LocalDateTime paymentAt;

    @Schema(description = "支付平台返回的支付流水号")
    private String transactionId;


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
