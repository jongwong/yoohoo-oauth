package cn.jongwong.server.config.security.jwt;

import cn.jongwong.server.dto.JwtUser;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtCodeAuthenticationProvider implements ReactiveAuthenticationManager {


    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        // Check if the token is of the correct type (SmsCodeAuthenticationToken)

        if (authentication instanceof JwtCodeAuthenticationToken) {
            // Cast the Authentication object to SmsCodeAuthenticationToken
            JwtCodeAuthenticationToken authenticationToken = (JwtCodeAuthenticationToken) authentication;


            try {
                JwtUser user = jwtUtil.validateToken(authenticationToken.getToken());


                // Create a new SmsCodeAuthenticationToken with the user, authorities, and token
                JwtCodeAuthenticationToken authenticationResult = new JwtCodeAuthenticationToken(authenticationToken.getToken(), null, user.getGrantedAuthorities());
                // Return a Mono of the new SmsCodeAuthenticationToken
                return Mono.just(authenticationResult);
            } catch (JwtException | IllegalArgumentException e) {
                throw new InvalidBearerTokenException(e.getMessage(), e.getCause());
            }


        }

        // Return Mono.empty() if the token type is not supported
        return Mono.empty();
    }


}
