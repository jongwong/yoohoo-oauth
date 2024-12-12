package cn.jongwong.server.config.security.sms;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;

@Component
public class SmsCodeAuthenticationProvider implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        // Check if the token is of the correct type (SmsCodeAuthenticationToken)

        if (authentication instanceof SmsCodeAuthenticationToken) {
            SmsCodeAuthenticationToken smsAuthToken = (SmsCodeAuthenticationToken) authentication;

            // Extract phone number and SMS code from the token
            String phoneNumber = (String) smsAuthToken.getPrincipal();
            String presentedCode = (String) smsAuthToken.getCredentials();

            // Perform validation on the phone number and code (e.g., check against a database or cache)
            if (isValidSmsCode(phoneNumber, presentedCode)) {
                // Create a new authentication token with user details and granted authorities
                Authentication auth = new SmsCodeAuthenticationToken(phoneNumber, presentedCode, getAuthorities());

                // Store authentication into the security context (ReactiveSecurityContextHolder)
                return Mono.just(auth)
                        .doOnSuccess(authentication1 -> {
                            // Store the authentication in the ReactiveSecurityContextHolder for the current context
                            ReactiveSecurityContextHolder.getContext()
                                    .flatMap(securityContext -> {

                                        securityContext.setAuthentication(authentication1);

                                        return Mono.just(securityContext.getAuthentication());
                                    });
                        });
            } else {
                // If validation fails, throw an exception
                throw new BadCredentialsException("Invalid SMS code");
            }
        }

        // Return Mono.empty() if the token type is not supported
        return Mono.empty();
    }

    private boolean isValidSmsCode(String phoneNumber, String presentedCode) {
        // Add your SMS code validation logic here (e.g., check against a database or cache)
        return true;  // For demonstration, assume always valid
    }

    private Collection<? extends GrantedAuthority> getAuthorities() {
        // Define roles or authorities for the authenticated user
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
