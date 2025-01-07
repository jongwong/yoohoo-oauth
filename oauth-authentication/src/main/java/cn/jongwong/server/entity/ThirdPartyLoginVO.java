package cn.jongwong.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "tb_third_party_login")
public class ThirdPartyLoginVO {

    @Id
    private String id = UUID.randomUUID().toString();

    private String userId;  // 系统用户的UUID

    private int provider;  // 第三方平台的枚举数字

    @Column("provider_user_id")
    private String thirdPartyUserId;  // 第三方平台上的用户ID



    private LocalDateTime expiresAt;  // 令牌的过期时间

    private LocalDateTime createdAt = LocalDateTime.now();  // 创建时间

    private LocalDateTime updatedAt = LocalDateTime.now();  // 更新时间

}
