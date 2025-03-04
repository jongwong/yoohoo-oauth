package cn.jongwong.server;

import cn.jongwong.server.config.wechatpay.WeChatPayService;
import cn.jongwong.server.entity.OrderVO;
import cn.jongwong.server.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

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


    @Autowired
    private WeChatPayService weChatPayService;

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
        var userVO = userService.getUserByMobileNumber(mobile).block();
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

    @Test
    public void testDemo1() throws Exception {
        var order = new OrderVO();
        weChatPayService.createJsApiOrder("owoZV7KyzmktjTlKSqiR1Ama5aYg", order);
    }


    // 请求日志过滤器
    private ExchangeFilterFunction logRequest() {
        return (request, next) -> {
            System.out.println("请求: " + request.method() + " " + request.url());
            request.headers().forEach((name, values) -> values.forEach(value -> System.out.println(name + "=" + value)));
            return next.exchange(request);
        };
    }

    // 响应日志过滤器
    private ExchangeFilterFunction logResponse() {
        return (request, next) -> next.exchange(request).doOnTerminate(() -> {
            System.out.println("响应: " + request.method() + " " + request.url());
        });
    }

    // 生成签名的方法
    private String generateSign(String appId, String timeStamp, String nonceStr, String outTradeNo, String apiKey) {
        String signStr = appId + "\n" + timeStamp + "\n" + nonceStr + "\n" + "prepay_id=" + outTradeNo + "\n";
        // 生成签名代码，RSA-SHA256 签名（在这里你需要实现 RSA 签名逻辑）
        return signWithRSA(signStr, apiKey);
    }

    // 使用 RSA 签名（简单示例，实际应使用私钥进行签名）
    private String signWithRSA(String signStr, String apiKey) {
        // 这里假设我们使用某个加密工具库进行签名
        return "签名结果";  // 实际签名过程需要用到 API 密钥或私钥进行签名
    }
}
//keystore cacerts -storepass changeit -noprompt -file ./DigiCertGlobalRootG2.crt -alias digicertglobalrootg2