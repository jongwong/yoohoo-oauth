package cn.jongwong.server.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Table("tb_consignee")
public class ConsigneeVO {
    @Schema(description = "主键ID")
    @Id
    private String id;

    @Schema(description = "收货人姓名")
    @NotNull(message = "收货人姓名不能为空")
    private String name;

    @Schema(description = "收货人性别，1=男，2=女")
    @NotNull(message = "收货人性别不能为空")
    private Integer sex;

    @Schema(description = "联系电话")
    @NotNull(message = "收货人联系电话不能为空")
    private String mobile;

    @Schema(description = "关联用户id")
    @NotNull(message = "关联用户id不能为空")
    private String userId;

}
