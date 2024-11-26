package cn.jongwong.oauth.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class CustomLogoutHandler implements ServerLogoutHandler {

    @Override
    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
        // Access ServerWebExchange
        ServerWebExchange serverWebExchange = exchange.getExchange();

        // Clear the security context in a reactive way
        Mono<Void> clearSecurityContext = Mono.fromRunnable(() -> {
            // Clear the authentication context
            SecurityContextHolder.clearContext(); // Clears the security context for the current request
        });

        // Optionally, log the logout or do additional work here
        clearSecurityContext.subscribe();

        // Optionally, you can log the successful logout or perform other actions
        System.out.println("User logged out successfully");

        // Return Mono to continue the chain, typically returning Mono.empty() means no further action
        return Mono.empty();
    }
}
