package cn.jongwong.server.dto;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Data
public class JwtUser implements Serializable {
    private static final long serialVersionUID = -339516038496531943L;

    private String id;

    private String username;


    private String name;

    private String nickname;

    private String[] stringAuthorities;

    private Collection<GrantedAuthority> authorities;

    // 将字符串数组转换为 GrantedAuthority 类型的集合
    public Collection<GrantedAuthority> getGrantedAuthorities() {
        Collection<GrantedAuthority> re = Arrays.stream(this.stringAuthorities)
                .map(SimpleGrantedAuthority::new)  // 将每个角色字符串转换为 SimpleGrantedAuthority
                .collect(Collectors.toList());    // 收集成 List<GrantedAuthority>

        return re;
    }

}
