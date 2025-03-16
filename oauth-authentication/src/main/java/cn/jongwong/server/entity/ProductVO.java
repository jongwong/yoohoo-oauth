package cn.jongwong.server.entity;

import cn.jongwong.server.dto.common.FileVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "tb_product")
public class ProductVO {

    @Id
    @Schema(description = "商品ID")
    private String id;

    @Schema(description = "商品编号")
    private Integer code;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品描述")
    private String description;


    @Schema(description = "类别ID")
    private String categoryId;

    @Schema(description = "类别代码")
    private Integer categoryCode;

    @Schema(description = "类别名称")
    private String categoryName;


    @Schema(description = "商品简短描述")
    private String shortDescription;

    @Schema(description = "价格价(sku中最低的市场价)")
    private Integer price;

    @Schema(description = "市场价(sku中最低的市场价)")
    private Integer marketPrice;


    @Schema(description = "SEO优化的标题")
    private String metaTitle;

    @Schema(description = "SEO优化的描述")
    private String metaDescription;

    @Schema(description = "SEO优化的关键词")
    private String metaKeywords;

    @Schema(description = "建档状态")
    private Integer archivedStatus;

    @Schema(description = "上架状态")
    private Integer listedStatus;

    @Schema(description = "审核拒绝原因")
    private String rejectionReason;


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


    @Schema(description = "商品主图")
    private FileVO mainImage;

    @Schema(description = "缩略图")
    private FileVO thumbnailImage;

    @Schema(description = "商品SKU")
    @Transient
    private List<ProductSkuVO> skus;

    @Schema(description = "是否有多个SKU")
    private Integer hasMultipleSku;


}
