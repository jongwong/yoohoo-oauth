package cn.jongwong.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootTest
public class PasswordEncoderTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void generateAndValidatePassword() {

        // 生成加密密码
        String rawPassword = "wwwwww";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("Encoded password: " + encodedPassword);

    }


}
