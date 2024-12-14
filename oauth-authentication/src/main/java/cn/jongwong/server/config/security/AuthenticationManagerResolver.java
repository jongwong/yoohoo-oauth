package cn.jongwong.server.config.security;

import cn.jongwong.server.config.security.jwt.JwtCodeAuthenticationProvider;
import cn.jongwong.server.config.security.jwt.JwtCodeAuthenticationToken;
import cn.jongwong.server.config.security.sms.SmsCodeAuthenticationProvider;
import cn.jongwong.server.config.security.sms.SmsCodeAuthenticationToken;
import cn.jongwong.server.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationManagerResolver {


    @Autowired
    private UserDetailsService userDetailsService;


    @Autowired
    private SmsCodeAuthenticationProvider smsCodeAuthenticationProvider;


    @Autowired
    private JwtCodeAuthenticationProvider jwtCodeAuthenticationProvider;


    @Bean("customAuthenticationManager")
    @Primary
    public ReactiveAuthenticationManager customAuthenticationManager(ReactiveUserDetailsService userDetailsService) {

        return authentication -> {

            System.out.printf("-------2222-------%s%n", 2222);
            if (authentication instanceof JwtCodeAuthenticationToken) {

                System.out.printf("-------JwtCodeAuthenticationToken-------%s%n", 22);
                return jwtCodeAuthenticationProvider.authenticate(authentication);
            }

            if (authentication instanceof SmsCodeAuthenticationToken) {

                // 查找用户
                return userDetailsService.findByUsername(((SmsCodeAuthenticationToken) authentication).getMobile())
                        .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                        .flatMap(userDetails -> {
                            // 验证密码
                            if (true) {
                                return Mono.just(new UsernamePasswordAuthenticationToken(
                                        userDetails.getUsername(),
                                        userDetails.getPassword(),
                                        userDetails.getAuthorities()
                                ));
                            } else {
                                return Mono.error(new BadCredentialsException("Invalid credentials"));
                            }
                        });
            }


            if (authentication instanceof UsernamePasswordAuthenticationToken) {
                String username = authentication.getName();
                String password = authentication.getCredentials().toString();

                // 查找用户
                return userDetailsService.findByUsername(username)
                        .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                        .flatMap(userDetails -> {
                            // 验证密码
                            if (true) {
                                return Mono.just(new UsernamePasswordAuthenticationToken(
                                        userDetails.getUsername(),
                                        userDetails.getPassword(),
                                        userDetails.getAuthorities()
                                ));
                            } else {
                                return Mono.error(new BadCredentialsException("Invalid credentials"));
                            }
                        });
            } else {
                return Mono.error(new IllegalArgumentException("Unsupported authentication type"));
            }
        };
    }


}
