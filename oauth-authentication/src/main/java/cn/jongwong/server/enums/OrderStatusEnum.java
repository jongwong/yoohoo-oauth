package cn.jongwong.server.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {

    PENDING_PAYMENT(10, "待支付"),
    PENDING_RECEIPT(20, "待收货"),
    REFUNDING(30, "退款中"),
    CANCELLED(40, "已取消"),
    PENDING_REVIEW(50, "待评价"),
    COMPLETED(60, "已完成");

    private final int code;
    private final String description;

    // 构造器
    OrderStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // 根据状态码获取状态描述
    public static String getDescriptionByCode(int code) {
        for (OrderStatusEnum status : OrderStatusEnum.values()) {
            if (status.getCode() == code) {
                return status.getDescription();
            }
        }
        return null;  // 如果找不到匹配的状态码，返回null
    }
}
