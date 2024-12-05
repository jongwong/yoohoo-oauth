package cn.jongwong.server.config.oauth;

import cn.jongwong.server.enums.SmsCodeTypeEnum;
import cn.jongwong.server.service.SmsCodeService;
import cn.jongwong.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SmsAuthenticationManager implements ReactiveAuthenticationManager {

    @Autowired
    SmsCodeService smsCodeService;
    @Autowired
    UserService userService;


    public Mono<Authentication> authenticate(Authentication authentication) {
        // 获取用户名和凭证（短信验证码）
        String phone = authentication.getName();
        String presentedCode = (String) authentication.getCredentials();
        // 异步验证短信验证码
        return this.smsCodeService.verifyCode(phone, presentedCode, SmsCodeTypeEnum.OAUTH_AUTHENTICATION)
                .flatMap(isValid -> {
                    if (isValid) {
                        // 验证成功后，构建用户详情
                        UserDetails userDetails = User.builder()
                                .username(phone)
                                .password("") // 短信登录无需密码
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))  // 设置用户角色
                                .build();

                        // 使用带权限的构造函数创建认证令牌
                        UsernamePasswordAuthenticationToken token =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        // 返回认证令牌
                        return Mono.just(token).doOnNext(t -> {
                            // 设置认证信息
                            ReactiveSecurityContextHolder.getContext().map(securityContext -> {
                                securityContext.setAuthentication(t);  // 设置认证信息到 SecurityContext
                                return securityContext;
                            });

                            System.out.println("认证信息已存储在 SecurityContext：" + t);
                        });
                    } else {
                        // 验证失败，返回错误
                        return Mono.error(new BadCredentialsException("验证码错误"));
                    }
                });
    }


}
