package cn.jongwong.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("tb_purchase_group")
public class PurchaseGroupVO {

    @Schema(description = "团购主键ID")
    private String id;

    @Schema(description = "团购名称")
    @NotNull(message = "名称不能为空")
    private String name;


    @Schema(description = "商品ID")
    @NotNull(message = "商品不能为空")
    private String productId;

    @Schema(description = "配送点ID")
    @NotNull(message = "配送不能为空")
    private String distributionPointId;

    @Schema(description = "最大参与人数")
    private Integer maxParticipants;

    @Schema(description = "当前参与人数")
    private Integer currentParticipants;

    @Schema(description = "团购状态 (1: 开团中, 2: 开团成功, 3: 已结束)")
    private Integer status;

    @Schema(description = "是否启用（1：启用，0：停用）")
    private Integer enable;

    @Schema(description = "团购描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "创建人ID")
    private String createdBy;

    @Schema(description = "创建人名称")
    private String createdByName;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "更新人ID")
    private String updatedBy;

    @Schema(description = "更新人名称")
    private String updatedByName;

    @Schema(description = "开始配送时间")
    @NotNull(message = "开始配送时间不能为空")
    private LocalDateTime timeDeliveryStart;

    @Schema(description = "配送截止时间")
    @NotNull(message = "配送截止时间不能为空")
    private LocalDateTime timeDeliveryEnd;

    @Schema(description = "开始时间")
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime timeStart;

    @Schema(description = "结束时间")
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime timeEnd;

    @Schema(description = "商品类别ID")
    private String categoryId;
}
