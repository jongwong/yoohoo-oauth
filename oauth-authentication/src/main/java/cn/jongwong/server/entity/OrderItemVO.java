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
@Table(name = "tb_order_items")
public class OrderItemVO {

    @Schema(description = "订单明细ID")
    @Id
    private String id;

    @Schema(description = "关联订单ID")
    private String orderId;


    @Schema(description = "明细类型 (1: 商品, 2: 配送费, 3: 优惠券)")
    private Integer type;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "关联ID")
    private String refId;

    @Schema(description = "关联代码")
    private Integer refCode;

    @Schema(description = "关联名称")
    private String refName;

    @Schema(description = "关联单据的类型")
    private Integer refType;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
