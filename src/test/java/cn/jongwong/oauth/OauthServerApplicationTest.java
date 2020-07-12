package cn.jongwong.oauth;

import cn.jongwong.oauth.entity.CustomOauth2User;
import cn.jongwong.oauth.entity.User;
import cn.jongwong.oauth.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class OauthServerApplicationTest {


    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    public void redisConnect() {
        String test = stringRedisTemplate.opsForValue().get("test");
        System.out.println(test);
    }

    @Test
    public void redisSet() {
        User user = new User();
        user.setId("1");
        user.setUsername("usernameA");
        user.setAvatar("A====");
        redisTemplate.opsForValue().set(user.getId(), user, 10, TimeUnit.MINUTES);
    }

    @Test
    public void redisGet() {
        System.out.println("--------------------");
        User user = (User) redisTemplate.opsForValue().get("1");
        System.out.println("=====================");
        System.out.println(user);
    }

    @Test
    public void getUserByPhoneNumber() {
        String phone = "18060601823";
        User user = userService.getUserByPhoneNumber(phone);
        System.out.println(phone);
    }
}
