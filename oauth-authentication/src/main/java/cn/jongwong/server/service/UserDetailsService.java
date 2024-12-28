package cn.jongwong.server.service;


import cn.jongwong.server.dto.CustomOauth2User;
import cn.jongwong.server.entity.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;

@Service
public class UserDetailsService implements ReactiveUserDetailsService {

    private final PasswordEncoder passwordEncoder;


    @Autowired
    UserService userService;


    @Autowired
    public UserDetailsService(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userService.getUserByIdentifier(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with username: " + username)))
                .flatMap(user -> {
                    return Mono.just(createCustomOauth2User(user));
                });
    }


    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER,ROLE_ADMIN");
    }

    public Mono<UserDetails> findByMobile(String mobile) {
        return userService.getUserByMobileNumber(mobile)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with mobile: " + mobile)))
                .flatMap(user -> {
                    return Mono.just(createCustomOauth2User(user));
                });
    }

    private CustomOauth2User createCustomOauth2User(UserVO findUserVO) {
        // 将数据库中的用户数据转换为自定义的 CustomOauth2User
        CustomOauth2User user = new CustomOauth2User();
        user.setUserName(findUserVO.getUsername());
        user.setPassword(findUserVO.getPassword());
        user.setRoles(Arrays.asList(findUserVO.getAuthorities()));
        user.setEnabled(1 == findUserVO.getEnabled());
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);


        return user;
    }


}
