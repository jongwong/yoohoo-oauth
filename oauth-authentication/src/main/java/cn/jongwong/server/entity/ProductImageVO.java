package cn.jongwong.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "tb_product_images") // 映射数据库表 tb_product_images
public class ProductImageVO {

    @Id
    @Schema(description = "主键ID，UUID格式")
    private String id;

    @Schema(description = "关联商品ID，UUID格式")
    private String productId;

    @Schema(description = "图片名称")
    private String name;

    @Schema(description = "图片访问的URL地址")
    private String url;

    @Schema(description = "图片类型")
    private Integer imageType;

}
