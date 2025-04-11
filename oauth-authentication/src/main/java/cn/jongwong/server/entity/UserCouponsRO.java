package cn.jongwong.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserCouponsRO {


    @Id
    @Schema(description = "用户优惠券ID")
    private String id;

    @Schema(description = "用户ID")
    private String userId;


    @Schema(description = "优惠券ID")
    private String couponsId;  // 改为 coupons_id

    @Schema(description = "是否已使用 (0: 未使用, 1: 已使用)")
    private Boolean isUsed;

    @Schema(description = "使用时间 (仅在已使用时有值)")
    private LocalDateTime usedAt;  // 改为 coupons_used_at

    @Schema(description = "优惠券有效期开始时间")
    private LocalDateTime validFrom;  // 改为 coupons_valid_from

    @Schema(description = "优惠券有效期结束时间")
    private LocalDateTime validTo;  // 改为 coupons_valid_to

    @Schema(description = "动态适用范围类型 (0: 分类, 1: 商品, 2: 品牌, NULL 表示无动态范围)")
    @Transient
    private Integer dynamicScopeType;

    @Schema(description = "动态适用范围ID")
    @Transient
    private String dynamicScopeId;


    // 以下是优惠券的数据字段
    @Schema(description = "优惠券名称")
    @Transient
    private String couponsName;  // 改为 coupons_name

    @Schema(description = "优惠券类型 (0: 折扣券, 1: 现金券, 2: 百分比折扣券)")
    @Transient
    private Integer couponsType;  // 改为 coupons_type

    @Schema(description = "折扣金额（现金券或满减券）")
    @Transient
    private BigDecimal discountAmount;  // 改为 discount_amount

    @Schema(description = "折扣百分比 (百分比折扣券, 如 10 表示 10%)")
    @Transient
    private BigDecimal discountPercentage;  // 改为 discount_percentage

    @Schema(description = "使用的最低消费金额")
    @Transient
    private BigDecimal minSpend = BigDecimal.ZERO;  // 改为 min_spend

    @Schema(description = "折扣上限 (针对百分比折扣)")
    @Transient
    private BigDecimal maxDiscount;  // 改为 max_discount

    @Schema(description = "优惠券活动生效时间")
    @Transient
    private LocalDateTime couponsValidFrom;  // 改为 coupons_valid_from

    @Schema(description = "优惠券活动失效时间")
    @Transient
    private LocalDateTime couponsValidTo;  // 改为 coupons_valid_to

    @Schema(description = "优惠券状态 (0: 草稿, 1: 审核中, 2: 审核拒绝, 3: 审核通过, 4: 已过期)")
    @Transient
    private Integer couponsStatus;  // 改为 coupons_status

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;  // 改为 coupons_issued_at


    @Schema(description = "创建人id")
    private String createdBy;


    @Schema(description = "创建人id")
    private String createdByName;
}

