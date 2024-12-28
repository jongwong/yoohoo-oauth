package cn.jongwong.server.config.security.handle;

import cn.jongwong.server.util.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class CustomGlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // 构建自定义响应
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add("Content-Type", "application/json");
        Response<Void> result;
        if (ex instanceof IllegalArgumentException) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            result = new Response<>(HttpStatus.BAD_REQUEST.value(), "Invalid request: " + ex.getMessage());
        } else if (ex instanceof InvalidBearerTokenException) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            result = new Response<>(HttpStatus.UNAUTHORIZED.value(), "Token Invalid: " + ex.getMessage());
        } else if (ex instanceof AuthenticationException) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            result = new Response<>(HttpStatus.UNAUTHORIZED.value(), "Authentication failed: " + ex.getMessage());
        } else {

            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            result = new Response<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred.");
        }

        HttpStatusCode s = exchange.getResponse().getStatusCode();


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
