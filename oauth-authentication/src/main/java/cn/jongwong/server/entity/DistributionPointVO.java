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
    private String id;

    private String name;

    private String address;

    private String contactPhone;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String deliveryTimeNote;

    private Integer enable;

    private Integer amountDelivery;


    private String createdBy;
    private String createdByName;

    private LocalDateTime createdAt;


    private String updatedBy;
    private String updatedByName;

    private LocalDateTime updatedAt;


}