package cn.jongwong.server.config.oauth;

import cn.jongwong.server.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
public class WebSecurityConfig {


    @Autowired
    ObjectMapper objectMapper;


    @Bean
    public SecurityWebFilterChain authorizationServerSecurityWebFilterChain(ServerHttpSecurity http) {


        http.formLogin((formLoginSpec) -> formLoginSpec.loginPage("/login"));


        http.csrf(s -> s.disable())

                .authorizeExchange(s ->
                        s.pathMatchers("/public/**", "/static/**", "/login", "/ping", "/oauth2/token", "/oauth2/authorize", "/favicon.ico", "/callback", "/authentication/form/sms/send", "/authentication/form/sms").permitAll()
                                .anyExchange().authenticated());


        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(withDefaults())
        );

//                .exceptionHandling((exceptionHandling) ->
//                        exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint)
//                                .accessDeniedHandler(customAccessDeniedHandler))
//        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        ; // Optional: Customize JWT converter


        return http.build();


    }


    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        // 使用授权服务器生成的公钥验证 JWT
        String jwkSetUri = "http://localhost:8080/oauth2/jwks"; // 替换为你的 JWKS 地址
        return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Autowired
    UserService userService;


}
