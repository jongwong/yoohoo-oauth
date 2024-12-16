package cn.jongwong.server.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserRes implements Serializable {
    private static final long serialVersionUID = -339516038496531943L;

    private String id;

    private String username;


    private String mobile;

    private String email;

    private String avatar;

    private int expired;

    private int locked;

    private int enabled;

    private String name;

    private String nickname;

    private String[] authorities;

    private LocalDateTime lastLoginAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
