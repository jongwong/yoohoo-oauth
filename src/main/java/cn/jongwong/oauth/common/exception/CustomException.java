package cn.jongwong.oauth.common.exception;

import org.springframework.security.oauth2.common.exceptions.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CustomException extends RuntimeException {
    public static final String ERROR = "error";
    public static final String ACCESS_DENIED = "access_denied";
    private Map<String, String> additionalInformation = null;

    public CustomException(String msg, Throwable t) {
        super(msg, t);
    }

    public CustomException(String msg) {
        super(msg);
    }

    public String getOAuth2ErrorCode() {
        return "invalid_request";
    }

    public int getHttpErrorCode() {
        return 400;
    }

    public Map<String, String> getAdditionalInformation() {
        return this.additionalInformation;
    }

    public void addAdditionalInformation(String key, String value) {
        if (this.additionalInformation == null) {
            this.additionalInformation = new TreeMap();
        }

        this.additionalInformation.put(key, value);
    }

    public static OAuth2Exception create(String errorCode, String errorMessage) {
        if (errorMessage == null) {
            errorMessage = errorCode == null ? "OAuth Error" : errorCode;
        }

        if ("invalid_client".equals(errorCode)) {
            return new InvalidClientException(errorMessage);
        } else {
            return (OAuth2Exception) ("access_denied".equals(errorCode) ? new UserDeniedAuthorizationException(errorMessage) : new OAuth2Exception(errorMessage));
        }
    }

    public static OAuth2Exception valueOf(Map<String, String> errorParams) {
        String errorCode = (String) errorParams.get("error");
        String errorMessage = errorParams.containsKey("error_description") ? (String) errorParams.get("error_description") : null;
        OAuth2Exception ex = create(errorCode, errorMessage);
        Set<Map.Entry<String, String>> entries = errorParams.entrySet();
        Iterator var5 = entries.iterator();

        while (var5.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) var5.next();
            String key = (String) entry.getKey();
            if (!"error".equals(key) && !"error_description".equals(key)) {
                ex.addAdditionalInformation(key, (String) entry.getValue());
            }
        }

        return ex;
    }

    public String toString() {
        return this.getSummary();
    }

    public String getSummary() {
        StringBuilder builder = new StringBuilder();
        String delim = "";
        String error = this.getOAuth2ErrorCode();
        if (error != null) {
            builder.append(delim).append("error=\"").append(error).append("\"");
            delim = ", ";
        }

        String errorMessage = this.getMessage();
        if (errorMessage != null) {
            builder.append(delim).append("error_description=\"").append(errorMessage).append("\"");
            delim = ", ";
        }

        Map<String, String> additionalParams = this.getAdditionalInformation();
        if (additionalParams != null) {
            for (Iterator var6 = additionalParams.entrySet().iterator(); var6.hasNext(); delim = ", ") {
                Map.Entry<String, String> param = (Map.Entry) var6.next();
                builder.append(delim).append((String) param.getKey()).append("=\"").append((String) param.getValue()).append("\"");
            }
        }

        return builder.toString();
    }
}

