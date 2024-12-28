package cn.jongwong.server.dto.authentication;

import lombok.Data;

@Data
public class AuthorizationFormDTO {
    private String client_id;
    private String redirect_uri;
    private String response_type;
    private String scope;
    private String state;
    private boolean approve;

    // Getters and Setters
}
