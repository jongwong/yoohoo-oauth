package cn.jongwong.server.enums.product;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品建档状态枚举
 * 描述商品在建档流程中的状态，例如草稿、审核中、审核完成等。
 */
@Schema(description = " 商品建档状态")
public enum ProductArchivedStatus {
    DRAFT(10, "草稿", "商品尚未提交审核，仅处于编辑阶段"),
    REVIEWING(20, "审核中", "商品正在审核流程中，等待审核人员操作"),
    REJECTED(30, "审核拒绝", "商品审核失败，需要修改后重新提交"),
    COMPLETED(40, "审核完成", "商品审核通过，建档流程结束");

    private final int code;          // 状态码
    private final String name;       // 状态名称
    private final String description; // 状态描述

    ProductArchivedStatus(int code, String name, String description) {
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

    public static ProductArchivedStatus fromCode(int code) {
        for (ProductArchivedStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的建档状态码: " + code);
    }
}
