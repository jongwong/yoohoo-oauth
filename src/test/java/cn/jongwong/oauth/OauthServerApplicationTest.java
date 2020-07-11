package cn.jongwong.oauth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class OauthServerApplicationTest {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void redisConnect() {
        String test = stringRedisTemplate.opsForValue().get("test");
        System.out.println(test);
    }
}
