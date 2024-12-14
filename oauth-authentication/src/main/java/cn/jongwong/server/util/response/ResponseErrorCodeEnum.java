package cn.jongwong.server.util.response;

public enum ResponseErrorCodeEnum {
    SUCCESS(0, "success"),                  // 请求成功
    BAD_REQUEST(400, "Bad Request"),        // 请求错误
    UNAUTHORIZED(401, "Unauthorized"),      // 未授权
    FORBIDDEN(403, "Forbidden"),            // 禁止访问
    NOT_FOUND(404, "Not Found"),            // 未找到
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"); // 服务器内部错误

    private final int code;
    private final String message;


    ResponseErrorCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public int getCode() {
        return this.code;
    }

}
