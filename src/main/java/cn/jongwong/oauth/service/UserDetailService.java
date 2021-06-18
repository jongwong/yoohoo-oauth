package cn.jongwong.oauth.service;


import cn.jongwong.oauth.entity.CustomOauth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    public String getId() {
        return "";
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        cn.jongwong.oauth.entity.User findUser = userService.getUserByIdentifier(userId);
        CustomOauth2User user = new CustomOauth2User();
        user.setUserName(findUser.getId());
        user.setPassword(findUser.getPassword());

        return new User(findUser.getId(), user.getPassword(), user.isEnabled(),
                user.isAccountNonExpired(), user.isCredentialsNonExpired(),
                user.isAccountNonLocked(), AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }
}
