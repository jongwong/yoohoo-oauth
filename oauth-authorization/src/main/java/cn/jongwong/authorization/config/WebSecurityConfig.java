package cn.jongwong.authorization.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // 配置认证规则

        http.csrf(s -> s.disable())
                .authorizeExchange(authorize -> authorize.pathMatchers("/login**", "/error").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2Login(oAuth2LoginSpec -> oAuth2LoginSpec.clientRegistrationRepository(clientRegistrationRepository)).logout(logout -> logout.logoutUrl("/logout"))
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

        ;


        return http.build();

    }

    @Autowired
    private ReactiveClientRegistrationRepository clientRegistrationRepository;


}
