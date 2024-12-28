package cn.jongwong.server.enums.product;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "商品图片类型")
public enum ProductImageType {

    @Schema(description = "主图", example = "1")
    MAIN_IMAGE(1),

    @Schema(description = "缩略图", example = "2")
    THUMBNAIL(2),

    @Schema(description = "轮播图", example = "3")
    CAROUSEL(3),

    @Schema(description = "其他图片", example = "4")
    OTHER(4);

    private final int code;

    ProductImageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
