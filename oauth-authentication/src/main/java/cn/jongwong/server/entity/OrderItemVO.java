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


    @Schema(description = "明细类型 (1: 商品")
    private Integer type;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "商品ID")
    private String productId;

    @Schema(description = "团购商品ID")
    private String groupProductId;

    @Schema(description = "商品代码")
    private Integer productCode;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品图片url")
    private String productImageUrl;


    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;


}
