package cn.jongwong.server.dto.authentication;


import lombok.Data;

@Data
public class AuthenticationSmsSendResponse {
    private long expireIn;
}
