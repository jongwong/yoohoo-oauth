package cn.jongwong.oauth;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordTest {

    @Test
    public void generateEncryptedPassword() {
        // 创建一个PasswordEncoder实例（BCrypt加密）
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // 定义明文密码
        String rawPassword = "wwwwww";

        // 生成加密后的密码
        String encryptedPassword = passwordEncoder.encode(rawPassword);

        // 输出加密后的密码
        System.out.println("Encrypted Password: " + encryptedPassword);

        // 你可以将生成的加密密码直接插入数据库
    }



}
