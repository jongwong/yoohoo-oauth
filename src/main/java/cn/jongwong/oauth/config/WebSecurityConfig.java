package cn.jongwong.oauth.config;

import cn.jongwong.oauth.service.UserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;


@Configuration
public class WebSecurityConfig {

    public WebSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    private final UserDetailsService userDetailsService;


    // 配置 SecurityWebFilterChain
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers("/login", "/logout", "logout-success", "/authentication/form").permitAll()
                .matchers(ServerWebExchangeMatchers.pathMatchers("/admin/**")).hasRole("ADMIN") // Secure "/admin/**"
                .anyExchange().authenticated()
                .and().formLogin().loginPage("/login")   // Custom login page
                .authenticationSuccessHandler(this::customAuthenticationSuccessHandler)  // Custom success handler
                .authenticationFailureHandler(this::customAuthenticationFailureHandler)
                .and()
                .logout()
                .logoutUrl("/logout");


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);  // Create the AuthenticationManager
    }

    private Mono<Void> customAuthenticationSuccessHandler(WebFilterExchange exchange, Authentication authentication) {
        // Log successful authentication (optional)
        System.out.println("Authentication successful for user: " + authentication.getName());

        // 获取 URL 中的 "redirect" 参数
        String redirectUrl = exchange.getExchange().getRequest().getQueryParams().getFirst("redirect_uri");

        // 如果没有提供 "redirect" 参数，则默认跳转到 /home 页面
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            redirectUrl = "/home";
        }

        // Optionally, log the redirect URL (optional)
        System.out.println("Redirecting to: " + redirectUrl);

        // 设置响应状态码为 302（重定向）
        exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);

        // 设置重定向的目标 URL
        exchange.getExchange().getResponse().getHeaders().setLocation(URI.create(redirectUrl));

        // 完成响应
        return exchange.getExchange().getResponse().setComplete();
    }


    // Custom Authentication Failure Handler using WebFilterExchange
    private Mono<Void> customAuthenticationFailureHandler(WebFilterExchange exchange, AuthenticationException exception) {
        // Log failure (optional)
        System.out.println("Authentication failed: " + exception.getMessage());

        // Optionally, you can provide a custom error message or redirect to a custom error page
        exchange.getExchange().getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);  // 401 Unauthorized
        exchange.getExchange().getResponse().getHeaders().setLocation(URI.create("/login?error=true")); // Redirect back to login with error

        return exchange.getExchange().getResponse().setComplete();  // End the exchange
    }

}
