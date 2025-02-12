package cn.jongwong.server.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {

    PENDING_PAYMENT(10, "待支付"),            // 订单状态 10 - 待支付
    PENDING_DELIVERY(20, "待配送"),           // 订单状态 20 - 待配送
    PREPARING(30, "备餐中"),                  // 订单状态 30 - 备餐中
    IN_DELIVERY(40, "配送中"),                // 订单状态 40 - 配送中
    COMPLETED(50, "已完成"),                  // 订单状态 50 - 已完成
    CANCELLED(60, "已取消"),                  // 订单状态 60 - 已取消
    REFUND_IN_PROGRESS(70, "退款中"),         // 订单状态 70 - 退款中
    REFUNDED(80, "已退款"),                   // 订单状态 80 - 已退款
    REFUND_FAILED(90, "退款失败");            // 订单状态 90 - 退款失败

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
