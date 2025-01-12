package cn.jongwong.server.entity;

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
@Table("tb_distribution_points")
public class DistributionPointVO {

    @Id
    private String id; // 配送点ID

    private String name; // 配送点名称

    private String address; // 配送点地址

    private String contactPhone; // 联系电话

    private BigDecimal latitude; // 纬度

    private BigDecimal longitude; // 经度

    private String deliveryTimeNote; // 配送时间备注

    private Integer enable; // 状态（1：启用，0：停用）

    private String createdBy;
    private String createdByName;

    private LocalDateTime createdAt;


    private String updatedBy;
    private String updatedByName;

    private LocalDateTime updatedAt;

}