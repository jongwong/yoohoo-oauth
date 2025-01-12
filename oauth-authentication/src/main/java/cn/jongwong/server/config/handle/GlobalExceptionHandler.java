package cn.jongwong.server.config.handle;

import cn.jongwong.server.util.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 参数验证异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<Response> handleWebExchangeBindException(WebExchangeBindException ex) {
        ex.printStackTrace();
        // 处理绑定异常
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return Mono.just(Response.error(500, fieldErrors.get(0).getDefaultMessage()));
    }


    /**
     * 兜底异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Mono<Response> handleMyException(Exception ex) {
        ex.printStackTrace();
        return Mono.just(Response.error(500, ex.getMessage()));
    }
}