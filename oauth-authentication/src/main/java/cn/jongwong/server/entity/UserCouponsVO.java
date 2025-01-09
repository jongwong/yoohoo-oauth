package cn.jongwong.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "tb_user_coupons")
public class UserCouponsVO {

    @Id
    @Schema(description = "用户优惠券ID")
    private String id;

    @Schema(description = "用户ID")
    private String userId;

    @Transient
    @Schema(description = "用户名称")
    private String userName;


    @Schema(description = "优惠券ID")
    private String couponsId;

    @Schema(description = "是否已使用 (0: 未使用, 1: 已使用)")
    private Boolean isUsed;


    @Schema(description = "使用时间 (仅在已使用时有值)")
    private LocalDateTime usedAt;

    @Schema(description = "优惠券有效期开始时间")
    private LocalDateTime validFrom;

    @Schema(description = "优惠券有效期结束时间")
    private LocalDateTime validTo;

    @Schema(description = "动态适用范围类型 (0: 分类, 1: 商品, 2: 品牌, NULL 表示无动态范围)")
    private Integer dynamicScopeType;

    @Schema(description = "动态适用范围ID")
    private String dynamicScopeId;


    @Schema(description = "发放时间")
    private LocalDateTime createdAt;
}
