package cn.jongwong.server;

import cn.jongwong.server.entity.User;
import cn.jongwong.server.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@WebAppConfiguration
public class OauthServerApplicationTest {


    @Autowired
    private UserService userService;


    @Resource
    private RedisTemplate redisTemplate;


    @Resource
    private ObjectMapper objectMapper;

    @Test
    public void redisConnect() {
//        String test = stringRedisTemplate.opsForValue().get("test");
//        System.out.println(test);
    }


    @Test
    public void getUserByMobileNumber() {
        String mobile = "18060601823";
        User user = userService.getUserByMobileNumber(mobile).block();
    }

    @Test
    public void DefaultSmsCodeSenderSend() {
//        DefaultSmsCodeSender defaultSmsCodeSender = new DefaultSmsCodeSender();
//        defaultSmsCodeSender.send("18060601823","123456");
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
