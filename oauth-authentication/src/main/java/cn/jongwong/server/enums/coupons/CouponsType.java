package cn.jongwong.server.enums.coupons;

public enum CouponsType {
    DISCOUNT(0, "折扣券"),
    CASH(1, "现金券"),
    PERCENTAGE(2, "百分比折扣券");

    private final int code;
    private final String description;

    CouponsType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
