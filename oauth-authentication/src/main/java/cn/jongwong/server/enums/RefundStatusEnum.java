package cn.jongwong.server.enums;

import lombok.Getter;

@Getter
public enum RefundStatusEnum {

    PENDING_REFUND(10, "退款中"),
    REFUND_SUCCESS(20, "退款成功"),
    REFUND_FAILED(30, "退款失败"),
    CANCELLED(40, "已取消");

    private final int code;
    private final String description;

    // 构造器
    RefundStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // 根据状态码获取状态描述
    public static String getDescriptionByCode(int code) {
        for (RefundStatusEnum status : RefundStatusEnum.values()) {
            if (status.getCode() == code) {
                return status.getDescription();
            }
        }
        return null;  // 如果找不到匹配的状态码，返回null
    }
}
