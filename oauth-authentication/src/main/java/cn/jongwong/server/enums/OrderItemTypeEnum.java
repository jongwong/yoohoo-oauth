package cn.jongwong.server.enums;

public enum OrderItemTypeEnum {

    PRODUCT(1, "商品"),
    DELIVERY(2, "配送费"),
    COUPON(3, "优惠券");

    private final int code;
    private final String description;

    // 构造方法
    OrderItemTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // 获取code
    public int getCode() {
        return code;
    }

    // 获取描述
    public String getDescription() {
        return description;
    }

    // 根据code获取枚举
    public static OrderItemTypeEnum fromCode(int code) {
        for (OrderItemTypeEnum type : OrderItemTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid OrderItemTypeEnum code: " + code);
    }

    @Override
    public String toString() {
        return "OrderItemTypeEnum{" +
                "code=" + code +
                ", description='" + description + '\'' +
                '}';
    }
}
