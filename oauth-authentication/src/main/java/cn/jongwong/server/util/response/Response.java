package cn.jongwong.server.util.response;

import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.io.Serializable;

@Getter
@Setter
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 1L;  // 添加 serialVersionUID

    private int code;
    private String message;
    private T data;


    // 全参构造函数
    public Response() {
    }

    // 全参构造函数
    public Response(int code, String message, T data) {
        this.data = data;
        this.message = message;
        this.code = code;
    }

    // 仅状态码和消息
    public Response(int code, String message) {
        this.message = message;
        this.code = code;
        this.data = null;
    }

    // 仅状态码和数据
    public Response(int code, T data) {
        this.data = data;
        this.code = code;
        this.message = "";
    }

    // 基于枚举
    public Response(ResponseErrorCodeEnum errorCode, T data) {
        this.code = errorCode.getCode();
        this.message = String.valueOf(errorCode.getMessage());
        this.data = data;
    }

    // 工厂方法
    public static <T> Response<Void> success() {
        return new Response<>(ResponseErrorCodeEnum.SUCCESS.getCode(), "Success", null);
    }

    public static <T> Mono<Response<T>> reactiveSuccess(Mono<T> data) {
        return data.flatMap(t -> Mono.just(new Response<>(ResponseErrorCodeEnum.SUCCESS.getCode(), "Success", t)));
    }


    // 工厂方法，带数据
    public static <T> Response<T> success(T data) {
        return new Response<>(ResponseErrorCodeEnum.SUCCESS.getCode(), "Success", data);
    }

    // 错误响应
    public static <T> Response<T> error(int code, String message) {
        return new Response<>(code, message, null);
    }

    // 错误响应，基于枚举
    public static <T> Response<T> error(ResponseErrorCodeEnum errorCode) {
        return new Response<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    // 错误响应，默认500
    public static <T> Response<T> error(String message) {
        return new Response<>(ResponseErrorCodeEnum.INTERNAL_SERVER_ERROR.getCode(), message, null);
    }

    // 错误响应，默认500
    public static <T> Response<T> notFound() {
        return new Response<>(ResponseErrorCodeEnum.NOT_FOUND.getCode(), ResponseErrorCodeEnum.NOT_FOUND.getMessage(), null);
    }


}
