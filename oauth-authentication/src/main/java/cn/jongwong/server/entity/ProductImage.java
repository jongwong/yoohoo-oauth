package cn.jongwong.server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table(name = "tb_product_images") // 映射数据库表 tb_product_images
public class ProductImage {

    @Id
    private String id; // 主键ID，UUID格式

    private String productId; // 关联商品ID，UUID格式

    private String name; // 图片名称

    private String url; // 图片访问的URL地址

    private Integer imageType; // 图片类型：1=主图，2=缩略图，3=轮播图，4=其他图片

    private LocalDateTime createdAt; // 创建时间

    private String createdBy; // 创建人ID，UUID格式

    private LocalDateTime updatedAt; // 更新时间

    private String updatedBy; // 更新人ID，UUID格式
}
