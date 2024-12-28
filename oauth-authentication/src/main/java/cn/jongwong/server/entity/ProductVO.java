package cn.jongwong.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
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

    @Schema(description = "商品简短描述")
    private String shortDescription;

    @Schema(description = "商品价格")
    private BigDecimal price;

    @Schema(description = "商品成本价格")
    private BigDecimal costPrice;

    @Schema(description = "商品的库存单位（SKU）")
    private String sku;

    @Transient
    @Schema(description = "商品状态")
    private Integer status;



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

    @Schema(description = "创建人ID")
    private String createdBy;


    @Transient
    @Schema(description = "创建人名称")
    private String createdByName;

    @Schema(description = "更新人ID")
    private String updatedBy;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt = LocalDateTime.now();


    @Transient
    @Schema(description = "更新人名称")
    private String updatedByName;

    @Schema(description = "审核拒绝原因")
    private String rejectionReason;

    @Transient
    @Schema(description = "商品主图")
    private List<ProductImageVO> mainImage;

    @Transient
    @Schema(description = "商品缩略图")
    private List<ProductImageVO> thumbnailImage;

    @Transient
    @Schema(description = "商品轮播图")
    private List<ProductImageVO> carouselImages;

    @Transient
    @Schema(description = "商品其他图片")
    private List<ProductImageVO> otherImages;


}
