package cn.jongwong.server.enums.product;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 团购状态枚举（0: 草稿中, 10: 待开团, 20: 开团中, 30: 开团成功, 40: 已结束）
 */
@Schema(description = "团购状态", type = "string")
public enum PurchaseGroupStatus {

    DRAFT(0, "草稿中", "团购处于草稿状态"),
    WAITING(10, "待开团", "团购尚未开始"),
    IN_PROGRESS(20, "开团中", "团购正在进行"),
    SUCCESS(30, "开团成功", "团购已成功开启"),
    ENDED(40, "已结束", "团购已经结束");

    private final int code;          // 状态码
    private final String name;       // 状态名称
    private final String description; // 状态描述

    // 构造函数
    PurchaseGroupStatus(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    // 获取状态码
    public int getCode() {
        return code;
    }

    // 获取状态名称
    public String getName() {
        return name;
    }

    // 获取状态描述
    public String getDescription() {
        return description;
    }

    // 根据状态码获取枚举
    public static PurchaseGroupStatus fromCode(int code) {
        for (PurchaseGroupStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的团购状态码: " + code);
    }
}
