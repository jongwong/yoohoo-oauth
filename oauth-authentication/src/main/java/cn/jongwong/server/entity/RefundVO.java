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
@Table(name = "tb_refund")
@Schema(description = "退款记录")
public class RefundVO {


    @Schema(description = "退款记录ID")
    @Id
    private String id;

    @Schema(description = "订单ID")
    private String orderId;

    @Schema(description = "退款方式 (1: 微信退款, 2: 退款宝, 3: 银行卡等)")
    private Integer paymentMethod; // 数据库中为 tinyint

    @Schema(description = "退款金额")
    private Integer amount;

    @Schema(description = "退款状态 (10: 退款中, 20: 退款成功, 30: 退款失败, 40: 已取消)")
    private Integer status; // 数据库中为 tinyint

    @Schema(description = "退款时间")
    private LocalDateTime refundAt;

    @Schema(description = "支付平台返回的退款流水号Id")
    private String transactionId;

    @Schema(description = "支付平台返回的退款流水号")
    private String transactionNo;

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


    @Schema(description = "审核人ID")
    private String auditBy;

    @Schema(description = "审核人名称")
    private String auditByName;

    @Schema(description = "审核时间")
    private LocalDateTime auditAt;

    @Schema(description = "审核原因")
    private String auditReason;

    @Schema(description = "申请原因")
    private String applyReason;
}
