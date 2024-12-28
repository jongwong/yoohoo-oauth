package cn.jongwong.server.enums.product;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品整体状态枚举
 * 结合建档状态和上架状态，描述商品的综合状态，例如草稿、审核中、已上架等。
 */
@Schema(description = "商品状态", type = "string")
public enum ProductStatus {
    DRAFT(0, "草稿", "商品尚未提交审核，仅处于编辑阶段"),
    IN_REVIEW(10, "审核中", "商品正在审核流程中，未被批准或拒绝"),
    REJECTED(20, "审核未通过", "商品审核失败，需要重新提交"),
    NOT_LISTED(30, "未上架", "商品已审核通过，但尚未上架"),
    LISTED(40, "已上架", "商品已审核通过并对用户可见"),
    UNLISTED(50, "已下架", "商品已从上架状态下架，用户不可见"),
    DISCONTINUED(60, "停售", "商品已停售，彻底停止销售"),
    DELETED(70, "已删除", "商品已被删除，通常为软删除，仅供后台管理查看");

    private final int code;          // 状态码
    private final String name;       // 状态名称
    private final String description; // 状态描述

    ProductStatus(int code, String name, String description) {
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

    public static ProductStatus fromCode(int code) {
        for (ProductStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的总体状态码: " + code);
    }
}
