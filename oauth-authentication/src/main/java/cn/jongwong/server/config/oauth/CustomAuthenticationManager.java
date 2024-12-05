package cn.jongwong.server.config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Primary // Indicate that this should be the default AuthenticationManager
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {

    @Autowired
    private SmsAuthenticationManager smsAuthenticationManager; // Inject custom SMS authentication manager

    private final ReactiveAuthenticationManager userDetailsAuthenticationManager;

    @Autowired
    public CustomAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
        // Use default UserDetailsRepositoryReactiveAuthenticationManager for username/password authentication
        this.userDetailsAuthenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {


        if (authentication instanceof SmsCodeAuthenticationToken) {
            System.out.println("---------Using SMS Authentication------");
            return smsAuthenticationManager.authenticate(authentication);
        }

        // Default case: Handle UsernamePasswordAuthenticationToken
        System.out.println("------ Using UserDetails Authentication------");
        return userDetailsAuthenticationManager.authenticate(authentication);
    }
}
