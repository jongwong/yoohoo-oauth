package cn.jongwong.server.config.handle;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ReactiveSecurityContextRepositoryImpl implements ServerSecurityContextRepository {

    private static final String SECURITY_CONTEXT_KEY = "SECURITY_CONTEXT";

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        // 从 WebSession 中加载 SecurityContext
        return exchange.getSession()
                .flatMap(session -> {
                    // 从 session 的属性中获取 SecurityContext
                    SecurityContext context = (SecurityContext) session.getAttributes().get(SECURITY_CONTEXT_KEY);
                    if (context != null) {
                        return Mono.just(context);
                    }
                    return Mono.empty(); // 如果没有找到 SecurityContext，返回空
                });
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        // 将 SecurityContext 保存到 WebSession 中
        return exchange.getSession()
                .flatMap(session -> {
                    session.getAttributes().put(SECURITY_CONTEXT_KEY, context);
                    return Mono.empty(); // 返回 Mono.empty() 表示保存完成
                });
    }

    public Mono<Void> remove(ServerWebExchange exchange) {
        // 从 WebSession 中移除 SecurityContext
        return exchange.getSession()
                .flatMap(session -> {
                    session.getAttributes().remove(SECURITY_CONTEXT_KEY);
                    return Mono.empty(); // 移除完成后返回 Mono.empty()
                });
    }
}
