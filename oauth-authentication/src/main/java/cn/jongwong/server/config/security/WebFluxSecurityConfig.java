package cn.jongwong.server.config.security;

import cn.jongwong.server.config.security.handle.CustomAuthenticationEntryPoint;
import cn.jongwong.server.config.security.handle.CustomAuthenticationFailureHandler;
import cn.jongwong.server.config.security.handle.CustomAuthenticationSuccessHandler;
import cn.jongwong.server.config.security.handle.JwtAuthenticationSuccessHandler;
import cn.jongwong.server.config.security.jwt.JwtAuthenticationWebFilter;
import cn.jongwong.server.config.security.sms.SmsAuthenticationWebFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder.withJwkSetUri;

@Configuration
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {

    public static final String[] WHITELIST_URLS = {
            "/login",
            "/authentication/form/sms/send",
            "/oauth2/token",
            "/auth/token",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/api-docs",
            "/api-docs/*",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/api-type"
    };
    @Autowired
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;


    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager reactiveAuthenticationManager) {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(reactiveAuthenticationManager);

        // 设置认证失败处理器
        authenticationWebFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);


        // 配置 SMS 认证过滤器
        SmsAuthenticationWebFilter smsAuthenticationWebFilter = new SmsAuthenticationWebFilter(reactiveAuthenticationManager, (ServerAuthenticationSuccessHandler) authenticationSuccessHandler, customAuthenticationFailureHandler);


        // 配置 SMS 认证过滤器
        JwtAuthenticationWebFilter jwtAuthenticationWebFilter = new JwtAuthenticationWebFilter(reactiveAuthenticationManager, (ServerAuthenticationSuccessHandler) jwtAuthenticationSuccessHandler, customAuthenticationFailureHandler);


        http.csrf(t -> t.disable());
        // 1. 首先放行 /login
        http.authorizeExchange(t -> t
                .pathMatchers(WHITELIST_URLS).permitAll()
                .pathMatchers("/admin/*").hasAuthority("ROLE_ADMIN")
                .anyExchange().authenticated());   // 其他路径需要认证
        // @formatter:off
        http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
        http.formLogin((form) -> form
                .loginPage("/login")
        );



        http.addFilterAt(smsAuthenticationWebFilter,SecurityWebFiltersOrder.AUTHENTICATION);

        http.addFilterAt(authenticationWebFilter,SecurityWebFiltersOrder.AUTHENTICATION);



        http.addFilterAt(jwtAuthenticationWebFilter,SecurityWebFiltersOrder.AUTHENTICATION);

        http.addFilterAt(authenticationWebFilter(reactiveAuthenticationManager), SecurityWebFiltersOrder.AUTHENTICATION);


        http.exceptionHandling(t -> t.authenticationEntryPoint(customAuthenticationEntryPoint));

        http.authenticationManager(reactiveAuthenticationManager);
        // @formatter:on
        return http.build();
    }


    @Bean
    @Primary
    public ReactiveJwtDecoder jwtDecoder() {
        String jwkSetUri = "http://101.34.152.79:4546/realms/dev1/protocol/openid-connect/certs";
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // 设置允许跨域的来源
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // 设置允许的请求方法
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // 设置允许的请求头
        configuration.setAllowCredentials(true); // 是否允许携带凭证
        configuration.setMaxAge(3600L); // 预检请求的缓存时间

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 设置全局跨域配置
        return source;
    }


}
