package cn.jongwong.oauth;

import cn.jongwong.oauth.entity.Secret;
import cn.jongwong.oauth.entity.User;
import cn.jongwong.oauth.service.SecretService;
import cn.jongwong.oauth.service.UserService;
import cn.jongwong.oauth.validate.code.ValidateCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class OauthServerApplicationTest {


    @Autowired
    private UserService userService;


    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private SecretService secretService;

    @Resource
    private ObjectMapper objectMapper;

    @Test
    public void redisConnect() {
//        String test = stringRedisTemplate.opsForValue().get("test");
//        System.out.println(test);
    }

    @Test
    public void redisSet() throws Exception {
        LocalDateTime dateTime = LocalDateTime.now();
        ValidateCode code = new ValidateCode("123456", 500);

        redisTemplate.opsForValue().set("123456", code, 10, TimeUnit.MINUTES);
    }

    @Test
    public void redisGet() throws Exception {
        System.out.println("--------------------");
        ValidateCode code = (ValidateCode) redisTemplate.opsForValue().get("123456");
        System.out.println("=====================");
        System.out.println(code);
        System.out.println(code.getExpireTime());
    }

    @Test
    public void getUserByPhoneNumber() {
        String phone = "18060601823";
        User user = userService.getUserByPhoneNumber(phone);
        System.out.println(phone);
    }

    @Test
    public void DefaultSmsCodeSenderSend() {
//        DefaultSmsCodeSender defaultSmsCodeSender = new DefaultSmsCodeSender();
//        defaultSmsCodeSender.send("18060601823","123456");
    }

    @Test
    public void getSecret() {
//        DefaultSmsCodeSender defaultSmsCodeSender = new DefaultSmsCodeSender();
//        defaultSmsCodeSender.send("18060601823","123456");
        Secret secret = secretService.getSecretById(1);
        System.out.println(secret);
    }

    @Test
    public void testDeserialize() throws IOException {
        LocalDateTime dateTime = LocalDateTime.now();
        Map<String, Object> testMap = new HashMap<String, Object>();
        testMap.put("name", "张三");
        testMap.put("age", 18);
        testMap.put("birthday", dateTime);
        String jsonStr = objectMapper.writeValueAsString(testMap);
        System.out.println("Map转为字符串：" + jsonStr);

        Map<String, Object> testMapDes = objectMapper.readValue(jsonStr, Map.class);
        System.out.println("字符串转Map：" + testMapDes);


    }
}
