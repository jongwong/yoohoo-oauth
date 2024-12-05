package cn.jongwong.server.config.oauth;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class SmsAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange.getFormData()
                .flatMap(formData -> {

                    try {
                        String phone = formData.getFirst("phone");
                        String code = formData.getFirst("code");
                        if (phone == null || code == null) {
                            return Mono.error(new IllegalArgumentException("Phone or code is missing"));
                        }
                        // 创建 PhoneSmsCodeAuthenticationToken
                        return Mono.just(new SmsCodeAuthenticationToken(
                                phone, code));
                    } catch (Exception e) {
                        return Mono.error(new IllegalArgumentException("Invalid request format"));
                    }
                });
    }
}

