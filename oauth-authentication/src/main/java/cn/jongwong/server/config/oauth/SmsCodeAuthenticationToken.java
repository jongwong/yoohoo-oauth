package cn.jongwong.server.config.oauth;


import org.springframework.security.authentication.AbstractAuthenticationToken;

public class SmsCodeAuthenticationToken extends AbstractAuthenticationToken {
    private String phone;
    private String code;

    public SmsCodeAuthenticationToken(String phone, String code) {
        super(null);
        this.phone = phone;
        this.code = code;
        setAuthenticated(false);  // 默认设置为未认证
    }

    @Override
    public Object getCredentials() {
        return this.code;
    }

    @Override
    public Object getPrincipal() {
        return this.phone;
    }
}
