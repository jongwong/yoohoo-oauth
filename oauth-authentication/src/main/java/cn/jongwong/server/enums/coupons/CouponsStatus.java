package cn.jongwong.server.enums.coupons;

public enum CouponsStatus {
    DRAFT(0, "草稿"),
    REVIEWING(10, "审核中"),
    REJECTED(20, "审核拒绝"),
    APPROVED(30, "审核通过");

    private final int code;
    private final String description;

    CouponsStatus(int code, String description) {
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

