package cn.jongwong.server.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WeChatRegistrationDTO {

    @NotNull(message = "encryptedData不能为空")
    @NotEmpty(message = "encryptedData不能为空")
    private String encryptedData; // 加密的手机号数据

    @NotNull(message = "iv不能为空")
    @NotEmpty(message = "iv不能为空")
    private String iv; // 向量


    @NotNull(message = "unionId不能为空")
    @NotEmpty(message = "unionId不能为空")
    private String unionId; // 会话密钥


    @NotNull(message = "姓名不能为空")
    @Size(min = 2, max = 8, message = "姓名长度应在 2 到 8 个字符之间")
    private String name; // 姓名

    @NotNull(message = "昵称不能为空")
    @Size(min = 2, max = 12, message = "昵称长度应在 2 到 12 个字符之间")
    private String nickname; // 昵称


    @NotNull(message = "头像不能为空")
    @Size(max = 500, message = "头像长度应小于 500 个字符之间")
    private String avatar;
}
