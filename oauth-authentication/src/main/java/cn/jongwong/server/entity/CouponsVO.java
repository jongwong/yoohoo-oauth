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
@Table(name = "tb_coupons")
public class CouponsVO {

    @Id
    @Schema(description = "优惠券ID")
    private String id;

    @Schema(description = "优惠券名称")
    private String name;

    @Schema(description = "优惠券类型 (0: 折扣券, 1: 现金券, 2: 百分比折扣券)")
    private Integer type; // Coupon type (0: Discount, 1: Cash Voucher, 2: Percentage Discount)

    @Schema(description = "折扣金额（现金券或满减券）")
    private BigDecimal discountAmount; // Discount amount (cash voucher or full discount)

    @Schema(description = "折扣百分比 (百分比折扣券, 如 10 表示 10%)")
    private BigDecimal discountPercentage; // Discount percentage (for percentage discount)

    @Schema(description = "使用的最低消费金额")
    private BigDecimal minSpend = BigDecimal.ZERO; // Minimum spend amount

    @Schema(description = "折扣上限 (针对百分比折扣)")
    private BigDecimal maxDiscount; // Max discount for percentage discount

    @Schema(description = "优惠券生效时间")
    private LocalDateTime validFrom;

    @Schema(description = "优惠券失效时间")
    private LocalDateTime validTo;

    @Schema(description = "发放总量")
    private Integer totalIssued = 0; // Total issued

    @Schema(description = "已使用数量")
    private Integer totalUsed = 0; // Total used

    @Schema(description = "已领取数量")
    private Integer totalClaimed = 0; // Total claimed


    @Schema(description = "优惠券状态 (0: 草稿, 1: 审核中, 2: 审核拒绝, 3: 审核通过, 4: 已过期)")
    private Integer status = 0; // Status: (0: Draft, 1: Under Review, 2: Rejected, 3: Approved, 4: Expired)


    @Schema(description = "审核拒绝原因 (仅在审核拒绝时有效)")
    private String rejectionReason; // Rejection reason (only when status is rejected)


    @Schema(description = "创建人ID")
    private String createdBy;

    @Schema(description = "创建人名称")
    private String createdByName;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新人ID")
    private String updatedBy;

    @Schema(description = "更新人名称")
    private String updatedByName;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;


}
