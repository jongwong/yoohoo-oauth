package cn.jongwong.oauth.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.HashMap;
import java.util.Map;

public class CustomAuthorizationCodeTokenGranter extends AbstractTokenGranter {
    private static final String GRANT_TYPE = "authorization_code";
    private final AuthorizationCodeServices authorizationCodeServices;

    public CustomAuthorizationCodeTokenGranter(AuthorizationServerTokenServices tokenServices, AuthorizationCodeServices authorizationCodeServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        this(tokenServices, authorizationCodeServices, clientDetailsService, requestFactory, "authorization_code");
    }

    protected CustomAuthorizationCodeTokenGranter(AuthorizationServerTokenServices tokenServices, AuthorizationCodeServices authorizationCodeServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authorizationCodeServices = authorizationCodeServices;
    }

    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = tokenRequest.getRequestParameters();
        String authorizationCode = (String) parameters.get("code");
        String redirectUri = (String) parameters.get("redirect_uri");
        if (authorizationCode == null) {
            throw new InvalidRequestException("An authorization code must be supplied.");
        } else {
            OAuth2Authentication storedAuth = this.authorizationCodeServices.consumeAuthorizationCode(authorizationCode);
            if (storedAuth == null) {
                throw new InvalidGrantException("Invalid authorization code: " + authorizationCode);
            } else {
                OAuth2Request pendingOAuth2Request = storedAuth.getOAuth2Request();
                String redirectUriApprovalParameter = (String) pendingOAuth2Request.getRequestParameters().get("redirect_uri");
                if ((redirectUri != null || redirectUriApprovalParameter != null) && !pendingOAuth2Request.getRedirectUri().equals(redirectUri)) {
                    throw new RedirectMismatchException("Redirect URI mismatch.");
                } else {
                    String pendingClientId = pendingOAuth2Request.getClientId();
                    String clientId = tokenRequest.getClientId();
                    if (clientId != null && !clientId.equals(pendingClientId)) {
                        throw new InvalidClientException("Client ID mismatch");
                    } else {
                        Map<String, String> combinedParameters = new HashMap(pendingOAuth2Request.getRequestParameters());
                        combinedParameters.putAll(parameters);
                        OAuth2Request finalStoredOAuth2Request = pendingOAuth2Request.createOAuth2Request(combinedParameters);
                        Authentication userAuth = storedAuth.getUserAuthentication();
                        return new OAuth2Authentication(finalStoredOAuth2Request, userAuth);
                    }
                }
            }
        }
    }
}

