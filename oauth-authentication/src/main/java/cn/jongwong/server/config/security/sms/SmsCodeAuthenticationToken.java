package cn.jongwong.server.config.security.sms;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class SmsCodeAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal; // 手机号
    private Object credentials; // 验证码

    @Getter
    private String mobile;
    @Getter
    private String code;


    public SmsCodeAuthenticationToken(String mobile, String code) {
        super(null);
        this.mobile = mobile;
        this.code = code;
        this.principal = mobile;
        this.credentials = code;
        setAuthenticated(false);
    }


    public SmsCodeAuthenticationToken(String mobile, String code, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.mobile = mobile;
        this.code = code;
        this.principal = mobile;
        this.credentials = code;
        setAuthenticated(true); // 标记为已认证
    }


    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }


}
