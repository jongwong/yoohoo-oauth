package cn.jongwong.server.config.security.sms;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class SmsAuthenticationWebFilter extends AuthenticationWebFilter {

    private static final String SMS_AUTH_URL = "/authentication/form/sms"; // 需要拦截的URL

    public SmsAuthenticationWebFilter(@Qualifier("customAuthenticationManager") ReactiveAuthenticationManager authenticationManager,
                                      ServerAuthenticationSuccessHandler authenticationSuccessHandler) {
        super(authenticationManager);
        setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(SMS_AUTH_URL));
        setServerAuthenticationConverter(new SmsCodeAuthenticationConverter());
        setAuthenticationSuccessHandler(authenticationSuccessHandler); // 确保执行成功处理器
    }

    // 将 SmsCodeAuthenticationConverter 定义为内部类
    private static class SmsCodeAuthenticationConverter implements ServerAuthenticationConverter {

        @Override
        public Mono<Authentication> convert(ServerWebExchange exchange) {
            // 从表单数据中提取手机号和验证码
            return exchange.getFormData()
                    .map(formData -> {
                        String mobile = formData.getFirst("mobile"); // 假设表单字段为 mobile
                        String code = formData.getFirst("code"); // 假设表单字段为 code
                        return new SmsCodeAuthenticationToken(mobile, code); // 创建 SmsCodeAuthenticationToken
                    });
        }
    }


}
