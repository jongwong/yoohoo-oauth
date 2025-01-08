package cn.jongwong.server.config.security.jwt;

import cn.jongwong.server.dto.user.CurrentAuthenticationUserRO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Setter
@Getter
public class JwtCodeAuthenticationToken extends UsernamePasswordAuthenticationToken implements Authentication {

    private String token;


    private CurrentAuthenticationUserRO currentUser;

    public JwtCodeAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public JwtCodeAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
