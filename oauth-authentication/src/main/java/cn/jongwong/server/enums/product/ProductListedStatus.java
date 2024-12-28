package cn.jongwong.server.enums.product;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品上架状态枚举
 * 描述商品在售卖流程中的状态，例如未上架、已上架、已下架等。
 */
@Schema(description = " 商品上架状态", type = "string")
public enum ProductListedStatus {
    NOT_LISTED(0, "未上架", "商品尚未上架，对用户不可见"),
    LISTED(1, "已上架", "商品已上架，对用户可见"),
    UNLISTED(2, "已下架", "商品已从上架状态中移除"),
    DISCONTINUED(3, "停售", "商品已停售，彻底停止销售");

    private final int code;          // 状态码
    private final String name;       // 状态名称
    private final String description; // 状态描述

    ProductListedStatus(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static ProductListedStatus fromCode(int code) {
        for (ProductListedStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的上架状态码: " + code);
    }
}
