package cn.jongwong.server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table(name = "tb_product_images")
public class ProductImage {

    @Id
    private String id; // 图片ID，UUID

    private String productId; // 商品ID，关联 `tb_products` 表的商品ID

    private String imageUrl; // 图片的URL

    private String imageType; // 图片类型（如：main=主图，gallery=其他图）

    private LocalDateTime createdAt; // 创建时间

    private LocalDateTime updatedAt; // 更新时间
}
