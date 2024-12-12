package cn.jongwong.server.config.security;

import cn.jongwong.server.config.security.sms.SmsAuthenticationWebFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

import static org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder.withJwkSetUri;

@Configuration
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {


    @Autowired
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager reactiveAuthenticationManager) {


        // 配置 SMS 认证过滤器
        SmsAuthenticationWebFilter smsAuthenticationWebFilter = new SmsAuthenticationWebFilter(reactiveAuthenticationManager, (ServerAuthenticationSuccessHandler) authenticationSuccessHandler);


        http.csrf(t -> t.disable());
        // 1. 首先放行 /login
        http.authorizeExchange(t -> t
                .pathMatchers("/login", "authentication/form/sms/send").permitAll()
                .pathMatchers("/test").hasAuthority("ROLE_ADMIN")
                .pathMatchers("/hello").authenticated()
                .anyExchange().authenticated());   // 其他路径需要认证
        // @formatter:off
        http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
        http.formLogin((form) -> form
                .loginPage("/login")
        );
        // 启用 OAuth2 资源服务器和 JWT 支持
        http.oauth2ResourceServer( t -> t.jwt(Customizer.withDefaults()));

        http.addFilterAt(smsAuthenticationWebFilter,SecurityWebFiltersOrder.AUTHENTICATION);

        http.addFilterAt(authenticationWebFilter(reactiveAuthenticationManager), SecurityWebFiltersOrder.AUTHENTICATION);

        http.authenticationManager(reactiveAuthenticationManager);
        // @formatter:on
        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        String jwkSetUri = "https://example.com/.well-known/jwks.json";  // Replace with your JWK set URI
        return withJwkSetUri(jwkSetUri).build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ServerSecurityContextRepository securityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }

    @Bean
    public AuthenticationWebFilter authenticationWebFilter(ReactiveAuthenticationManager authenticationManager) {
        // 创建一个自定义的 AuthenticationWebFilter
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);

        // 设置成功处理器为默认的 WebFilterChainServerAuthenticationSuccessHandler
        authenticationWebFilter.setAuthenticationSuccessHandler(new WebFilterChainServerAuthenticationSuccessHandler());

//        // 设置失败处理器（如果需要）
//        authenticationWebFilter.setAuthenticationFailureHandler(new WebFilterChainServerAuthenticationFailureHandler());

        return authenticationWebFilter;
    }

}
