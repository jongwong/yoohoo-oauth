package cn.jongwong.oauth.service;

import cn.jongwong.oauth.entity.CustomOauth2User;
import cn.jongwong.oauth.entity.User;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reactor.core.publisher.Mono;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        User findUser = userService.getUserByIdentifier(username);
        if (findUser == null) {
            return Mono.error(new UsernameNotFoundException("User not found with username: " + username));
        }
        return Mono.just(createCustomOauth2User(findUser));
    }

    private CustomOauth2User createCustomOauth2User(User findUser) {
        // 将数据库中的用户数据转换为自定义的 CustomOauth2User
        CustomOauth2User user = new CustomOauth2User();
        user.setUserName(findUser.getUsername());
        user.setPassword(findUser.getPassword());
        user.setEnabled(1 == findUser.getEnabled());
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(0 == findUser.getLocked());
        System.out.println(findUser);


        return user;
    }
}
