package cn.jongwong.server.config.wechatpay;

import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class WeChatPaySignature {

    public static String generateSign(String signData, String privateKeyPem) throws Exception {
        // 移除私钥中的头尾信息
        String privateKeyPemFormatted = privateKeyPem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\n", "");

        // 解码私钥
        byte[] encodedPrivateKey = Base64.getDecoder().decode(privateKeyPemFormatted);

        // 创建私钥对象
        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(encodedPrivateKey));

        // 使用RSA和SHA256进行签名
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(signData.getBytes("UTF-8"));

        // 获取签名结果并进行Base64编码
        byte[] signedData = signature.sign();
        return Base64.getEncoder().encodeToString(signedData);
    }
}
