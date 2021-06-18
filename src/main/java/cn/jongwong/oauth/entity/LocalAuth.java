package cn.jongwong.oauth.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalAuth {
    private String id;


    private String userId;


    private String identityType;

    private String identifier;


    private String credential;
}
