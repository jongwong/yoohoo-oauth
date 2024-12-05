package cn.jongwong.server.config.oauth;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.stereotype.Component;


@Component
public class SmsCodeAuthenticationFilter extends AuthenticationWebFilter {

    // No need for constructor injection here, just annotate the class with @Component
    // Autowiring handlers in the constructor
    public SmsCodeAuthenticationFilter(ReactiveAuthenticationManager authenticationManager) {

        super(authenticationManager);
        // Set the request matcher for the SMS authentication path
        setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/authentication/form/sms"));

        // Set the authentication converter to handle the SMS authentication token
        setServerAuthenticationConverter(new SmsAuthenticationConverter());


    }


}