package cn.jongwong.server.config.security.handle;

import cn.jongwong.server.util.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class CustomAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {


    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {

        // 获取响应对象
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

        // 设置响应状态码为 401 Unauthorized
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        // 创建自定义的错误响应
        Response<Void> result;

        // 根据不同的异常类型，返回不同的错误消息
        if (exception instanceof BadCredentialsException) {
            result = Response.error("Invalid credentials");
        } else if (exception instanceof InternalAuthenticationServiceException) {
            result = Response.error("Authentication service error");
        } else {
            result = Response.error("Authentication failed");
        }

        // 将响应转换为 JSON 格式，并写入响应体
        byte[] responseBody = result.toString().getBytes();
        return response.writeWith(Mono.just(response.bufferFactory().wrap(responseBody)));
    }
}
