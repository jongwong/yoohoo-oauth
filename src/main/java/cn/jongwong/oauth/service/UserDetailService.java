package cn.jongwong.oauth.service;

import cn.jongwong.oauth.entity.CustomOauth2User;
import cn.jongwong.oauth.entity.User;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // 从数据库获取用户
        User findUser = userService.getUserByIdentifier(userId);

        if (findUser == null) {
            throw new UsernameNotFoundException("User not found with username: " + userId);
        }

        // 创建并填充 CustomOauth2User 对象
        CustomOauth2User user = new CustomOauth2User();
        user.setUserName(findUser.getUsername());
        user.setPassword(findUser.getPassword());
        user.setEnabled("1".equals(findUser.getEnabled()));
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked("0".equals(findUser.getLocked()));


        return user;
    }
}
