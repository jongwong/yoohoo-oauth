package cn.jongwong.server.dto.authentication;


import lombok.Data;

@Data
public class AuthenticationFormSmsDTO {
    private String mobile;
    private String code;

}
