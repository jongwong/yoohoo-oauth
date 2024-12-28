package cn.jongwong.server.config.security.handle;

import cn.jongwong.server.util.response.Response;
import cn.jongwong.server.util.response.ResponseErrorCodeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;


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
            result = Response.error(ResponseErrorCodeEnum.UNAUTHORIZED.getCode(), "Invalid credentials");
        } else if (exception instanceof InvalidBearerTokenException) {
            result = Response.error(ResponseErrorCodeEnum.UNAUTHORIZED.getCode(), "Token invalid");
        } else if (exception instanceof InternalAuthenticationServiceException) {
            result = Response.error("Authentication service error");
        } else {
            result = Response.error("Authentication failed");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse;
        try {
            jsonResponse = objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            throw new RuntimeException("Error converting result to JSON", e);
        }
        // 将 JSON 字符串写入响应体
        byte[] responseBody = jsonResponse.getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(responseBody)));
    }
}
