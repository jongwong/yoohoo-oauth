package cn.jongwong.server.dto.authentication;


import lombok.Data;

@Data
public class AuthenticationFormSmsRequest {
    private String mobile;
    private String code;

}
