package cn.jongwong.server.service;


import cn.jongwong.server.dto.CustomOauth2User;
import cn.jongwong.server.entity.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Service
public class UserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    UserService userService;



    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userService.getUserWithPasswordByIdentifier(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with username: " + username)))
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
