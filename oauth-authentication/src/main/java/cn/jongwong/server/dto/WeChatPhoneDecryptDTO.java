package cn.jongwong.server.dto;


import lombok.Data;

@Data
public class WeChatPhoneDecryptDTO {
    private String encryptedData; // 加密的手机号数据
    private String iv; // 向量
    private String sessionKey; // 会话密钥
}
