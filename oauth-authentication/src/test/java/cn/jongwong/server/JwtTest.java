package cn.jongwong.server;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;

public class JwtTest {
    // 生成指定长度的随机密钥
    public static String generateKey(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[length];
        secureRandom.nextBytes(key);

        // 返回 Base64 编码的密钥
        return Base64.getEncoder().encodeToString(key);
    }

    @Test
    public void testGenerateKey() {
        // 调用生成 512 位（64 字节）密钥的方法
        String generatedKey = generateKey(64);

   
        System.out.println("生成的密钥: " + generatedKey);
    }
}
