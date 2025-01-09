package cn.jongwong.server.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserRO implements Serializable {

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

    private String authorities;

    private LocalDateTime lastLoginAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public String[] getAuthorities() {
        // 将逗号分隔的字符串转换为数组
        return authorities != null ? authorities.split(",") : new String[0];
    }

    public void setAuthorities(String[] tagsArray) {
        // 将数组转换为逗号分隔的字符串存储
        this.authorities = String.join(",", tagsArray);
    }
}
