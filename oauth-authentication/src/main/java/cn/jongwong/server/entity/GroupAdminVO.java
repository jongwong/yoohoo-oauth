package cn.jongwong.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("tb_group_admin")
@Schema(description = "管理员表")
public class GroupAdminVO {

    @Id
    @Schema(description = "管理员ID")
    private String id;

    @Schema(description = "关联用户ID")
    private String userId;
}
