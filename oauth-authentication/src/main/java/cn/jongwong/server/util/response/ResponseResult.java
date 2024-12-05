package cn.jongwong.server.util.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseResult<T> {
    private static final long serialVersionUID = 1L;  // 添加 serialVersionUID

    private int code;
    private String message;
    private T data;

    // 全参构造函数
    public ResponseResult(int code, String message, T data) {
        this.data = data;
        this.message = message;
        this.code = code;
    }

    // 仅状态码和消息
    public ResponseResult(int code, String message) {
        this.message = message;
        this.code = code;
        this.data = null;
    }

    // 仅状态码和数据
    public ResponseResult(int code, T data) {
        this.data = data;
        this.code = code;
        this.message = "";
    }

    // 基于枚举
    public ResponseResult(ResponseErrorCodeEnum errorCode, T data) {
        this.code = errorCode.getCode();
        this.message = String.valueOf(errorCode.getMessage());
        this.data = data;
    }

    // 工厂方法
    public static <T> ResponseResult<Void> success() {
        return new ResponseResult<>(ResponseErrorCodeEnum.SUCCESS.getCode(), "Success", null);
    }

    // 工厂方法，带数据
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(ResponseErrorCodeEnum.SUCCESS.getCode(), "Success", data);
    }

    // 错误响应
    public static <T> ResponseResult<T> error(int code, String message) {
        return new ResponseResult<>(code, message, null);
    }

    // 错误响应，基于枚举
    public static <T> ResponseResult<T> error(ResponseErrorCodeEnum errorCode) {
        return new ResponseResult<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    // 错误响应，默认500
    public static <T> ResponseResult<T> error(String message) {
        return new ResponseResult<>(ResponseErrorCodeEnum.INTERNAL_SERVER_ERROR.getCode(), message, null);
    }
}
