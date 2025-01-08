package cn.jongwong.server.entity;

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
@Table("tb_distribution_point_product")
public class DistributionPointProductVO {

    @Id
    private String id; // 关联ID

    private String pointId; // 配送点ID

    private String productId; // 商品ID

    private Integer quantity; // 商品库存数量

    private Integer status; // 状态（1：启用，0：关闭）

    private LocalDateTime createdAt; // 创建时间

    private LocalDateTime updatedAt; // 更新时间
}