package cn.jongwong.oauth.config;


import cn.jongwong.oauth.config.sms.SmsAuthenticationConfig;
import cn.jongwong.oauth.config.sms.SmsCodeAuthenticationFilter;
import cn.jongwong.oauth.handler.MyAuthenticationFailureHandler;
import cn.jongwong.oauth.handler.MyAuthenticationSucessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private MyAuthenticationSucessHandler myAuthenticationSucessHandler;
    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;


    @Autowired
    private SmsCodeAuthenticationFilter smsCodeAuthenticationFilter;

    @Autowired
    private SmsAuthenticationConfig smsAuthenticationConfig;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .addFilterBefore(smsCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin() // 表单登录
                // http.httpBasic() // HTTP Basic
                .loginPage("/authentication/require") // 登录跳转 URL
                .loginProcessingUrl("/authentication/form") // 处理表单登录 URL
                .successHandler(myAuthenticationSucessHandler)
                .failureHandler(myAuthenticationFailureHandler)
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .and()
                .authorizeRequests() // 授权配置
                .antMatchers(
                        "/",
                        "/authentication/require", "/authentication/form", "/authentication/mobile",
                        "/login.html",
                        "/logout",
                        "/img/*", "/css/*", "/js/*", "/code/sms",
                        "/authorize/sms",
                        "/.well-known/openid-configuration",
                        "/public/*",
                        "/verify").permitAll() // 登录跳转 URL 无需认证
                .anyRequest()  // 所有请求
                .authenticated() // 都需要认证
                .and()
                .cors()
                .and().csrf().disable()
                .apply(smsAuthenticationConfig);


    }
}
