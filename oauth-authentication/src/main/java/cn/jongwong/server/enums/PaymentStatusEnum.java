package cn.jongwong.server.enums;

import lombok.Getter;

@Getter
public enum PaymentStatusEnum {

    PENDING_PAYMENT(10, "支付中"),
    PAYMENT_SUCCESS(20, "支付成功"),
    PAYMENT_FAILED(30, "支付失败"),
    CANCELLED(40, "已取消");

    private final int code;
    private final String description;

    // 构造器
    PaymentStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // 根据状态码获取状态描述
    public static String getDescriptionByCode(int code) {
        for (PaymentStatusEnum status : PaymentStatusEnum.values()) {
            if (status.getCode() == code) {
                return status.getDescription();
            }
        }
        return null;  // 如果找不到匹配的状态码，返回null
    }
}
