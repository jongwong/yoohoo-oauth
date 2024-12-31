package cn.jongwong.server.service;

import cn.jongwong.server.config.WeChatConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.util.Map;

@Service
public class WeChatAuthService {

    @Autowired
    private WeChatConfig weChatConfig;

    @Autowired
    private WebClient webClient;

    // 模拟会话存储，这里使用一个 Map 进行存储，实际可以用 Redis 或数据库存储
    private final Map<String, String> sessionStore = new java.util.HashMap<>();

    // 登录处理：通过 code 获取 openid 和 session_key
    public Mono<Map<String, String>> wxLogin(String code) {
        // 打印 appid 和 secret
        String url = UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com/sns/jscode2session")
                .queryParam("appid", weChatConfig.getAppid())  // 使用配置中的 appid
                .queryParam("secret", weChatConfig.getSecret())  // 使用配置中的 secret
                .queryParam("js_code", code)  // 微信小程序返回的 code
                .queryParam("grant_type", "authorization_code")  // 固定参数
                .toUriString();

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class) // 获取响应体
                .flatMap(responseBody -> {
                    try {
                        // 解析 JSON 响应
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map<String, String> responseMap = objectMapper.readValue(responseBody, Map.class);

                        if (responseMap.containsKey("openid") && responseMap.containsKey("session_key")) {
                            // 提取 openid 和 session_key
                            String openid = responseMap.get("openid");
                            String sessionKey = responseMap.get("session_key");

                            // 存储会话信息到 sessionStore
                            sessionStore.put(openid, sessionKey);

                            // 返回 openid 和 session_key
                            return Mono.just(Map.of("openid", openid, "session_key", sessionKey));
                        } else {
                            return Mono.error(new RuntimeException("微信接口调用失败，返回: " + responseBody));
                        }
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("微信接口返回解析失败", e));
                    }
                });
    }

    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";

    // 解密手机号
    public Mono<String> decryptPhoneNumber(String encryptedData, String iv, String sessionKey) {
        return Mono.fromCallable(() -> {
            try {
                return decryptPhoneNumberInternal(encryptedData, iv, sessionKey);
            } catch (Exception e) {
                throw new RuntimeException("解密手机号失败", e);
            }
        });
    }

    // 实际解密手机号逻辑
    private String decryptPhoneNumberInternal(String encryptedData, String iv, String sessionKey) throws Exception {
        byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedData);
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        byte[] sessionKeyBytes = Base64.getDecoder().decode(sessionKey);

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
        SecretKey key = new javax.crypto.spec.SecretKeySpec(sessionKeyBytes, "AES");

        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

        byte[] decryptedData = cipher.doFinal(encryptedDataBytes);
        return new String(decryptedData, "UTF-8");
    }

    // 解密用户信息并获取手机号
    public Mono<String> getUserInfoAndPhoneNumber(String openid, String encryptedData, String iv) {
        return Mono.fromCallable(() -> {
            try {
                // 验证是否存在有效的 sessionKey
                String sessionKey = sessionStore.get(openid);
                if (sessionKey == null) {
                    throw new RuntimeException("会话已过期，请重新登录");
                }

                // 调用解密手机号方法
                return decryptPhoneNumberInternal(encryptedData, iv, sessionKey);
            } catch (Exception e) {
                throw new RuntimeException("解密手机号失败", e);
            }
        });
    }
}
