package cn.jongwong.server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Table(name = "tb_product")
public class Product {

    @Id
    private String id; // 商品ID，UUID

    private String name; // 商品名称

    private String description; // 商品描述

    private String shortDescription; // 商品简短描述

    private BigDecimal price; // 商品价格

    private BigDecimal costPrice; // 商品成本价格

    private String sku; // 商品的库存单位（SKU）


    private Integer status; // 商品状态（1=可用，2=不可用，3=停售）

    private LocalDateTime createdAt; // 创建时间


    private LocalDateTime upLocalDateTimedAt; // 更新时间

    private String metaTitle; // SEO优化的标题

    private String metaDescription; // SEO优化的描述

    private String metaKeywords; // SEO优化的关键词

    private Integer archivedStatus; // 建档状态（10=草稿，20=审核中，30=审核拒绝，40=建档完成）

    private Integer listedStatus; // 上架状态（0=未上架，1=已上架）

    private String createdBy; // 创建人ID（用户的UUID）

    private String createdByName; // 创建人名称

    private String updatedBy; // 更新人ID（用户的UUID）

    private String updatedByName; // 更新人名称


    // 主图
    private List<ProductImage> mainImage;

    // 缩略图
    private List<ProductImage> thumbnailImage;

    // 轮播图
    private List<ProductImage> carouselImages;

    // 其他图片
    private List<ProductImage> otherImages;

}
