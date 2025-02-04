package cn.jongwong.server.service;

import cn.jongwong.server.config.WeChatConfig;
import cn.jongwong.server.config.security.jwt.JwtUtil;
import cn.jongwong.server.controller.RedisService;
import cn.jongwong.server.entity.ThirdPartyLoginVO;
import cn.jongwong.server.entity.UserVO;
import cn.jongwong.server.repository.ThirdPartyLoginRepository;
import cn.jongwong.server.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class WeChatAuthService {

    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";


    @Autowired
    private WeChatConfig weChatConfig;



    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository; // 用户数据访问层

    @Autowired
    private RedisService redisService;

    @Autowired
    private PasswordEncoder passwordEncoder; // 密码加密工具

    @Autowired
    private ThirdPartyLoginRepository thirdPartyLoginRepository; // 第三方登录数据访问层
    @Autowired
    private JwtUtil jwtUtil;

    private String getSessionMapKey(String unionId) {
        return "UnionIdSessionMapKey:" + unionId;
        //"UnionIdSessionMapKey:" + responseMap.get("union_id")
    }

    @Transactional
    public Mono<Map<String, String>> wxLogin(String code) {
        // 构造微信登录 URL
        String url = UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com/sns/jscode2session")
                .queryParam("appid", weChatConfig.getAppid())
                .queryParam("secret", weChatConfig.getSecret())
                .queryParam("js_code", code)
                .queryParam("grant_type", "authorization_code")
                .toUriString();
        Map<String, String> mapRe = new HashMap<>();

        WebClient webClient = WebClient.builder().baseUrl("https://api.weixin.qq.com/").build();
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(responseBody -> {
                    // 解析微信接口返回的 JSON 数据
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        Map<String, String> responseMap = objectMapper.readValue(responseBody, Map.class);
                        if (responseMap.containsKey("openid") && responseMap.containsKey("session_key")) {
                            mapRe.put("union_id", responseMap.getOrDefault("unionid", null)); // unionid 可能为空
                            mapRe.put("session_key", responseMap.getOrDefault("session_key", null));

                            return Mono.just(responseMap);
                        } else {
                            return Mono.error(new RuntimeException("微信接口返回数据不完整: " + responseBody));
                        }
                    } catch (JsonProcessingException e) {
                        return Mono.error(new RuntimeException("JSON 解析失败: " + e.getMessage()));
                    }
                }).flatMap(map -> {
                    // 将 session_key 存入 Redis，并设置过期时间为 3600 秒
                    return redisService.set(getSessionMapKey(mapRe.get("union_id")), mapRe.get("session_key"), 3600)
                            .then(Mono.just(mapRe)); // 确保返回 rawP
                })
                .flatMap((e) -> {
                    String unionId = mapRe.get("union_id");
                    // 查询 ThirdPartyLogin
                    return thirdPartyLoginRepository.findByThirdPartyUserId(unionId)
                            .switchIfEmpty(Mono.defer(() -> {
                                // 如果不存在 ThirdPartyLogin，先创建用户
                                String createUserId = UUID.randomUUID().toString();
                                String encodedPassword = passwordEncoder.encode(UUID.randomUUID().toString());

                                UserVO newUser = UserVO.builder()
                                        .id(createUserId)
                                        .username(unionId)
                                        .password(encodedPassword)
                                        .enabled(1) // 默认启用
                                        .createdAt(LocalDateTime.now())
                                        .build();

                                return userService.createUser(newUser)
                                        .flatMap(user -> {
                                            // 插入 ThirdPartyLogin
                                            ThirdPartyLoginVO newParty = ThirdPartyLoginVO.builder()
                                                    .id(UUID.randomUUID().toString())
                                                    .createdAt(LocalDateTime.now())
                                                    .userId(user.getId())
                                                    .provider(1) // 微信平台 provider 标识
                                                    .thirdPartyUserId(unionId)
                                                    .build();

                                            // 插入 ThirdPartyLogin 并返回
                                            return thirdPartyLoginRepository.insert(newParty);
                                        });
                            }))
                            .flatMap(thirdParty -> {
                                mapRe.put("user_id", thirdParty.getUserId());
                                // 根据 user_id 查询用户信息
                                return userService.findById(thirdParty.getUserId());
                            })
                            .map(user -> {
                                // 根据用户信息生成 Token
                                var accessToken = jwtUtil.generateToken(user, false);
                                var refreshToken = jwtUtil.generateToken(user, true);

//                                JwtCodeAuthenticationToken authenticationToken = new JwtCodeAuthenticationToken(accessToken, null);
//
//                                authenticationToken.setToken(accessToken);
//
//                                // 将 Authentication 设置到 SecurityContext 中
//                                SecurityContextHolder.getContext().setAuthentication(authenticationToken);


                                mapRe.put("access_token", accessToken);
                                mapRe.put("refresh_token", refreshToken);
                                return mapRe;
                            });
                });
    }


    // 实际解密手机号逻辑
    private Map<String, String> decryptPhoneNumberInternal(String encryptedData, String iv, String sessionKey) throws Exception {

        byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedData);
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        byte[] sessionKeyBytes = Base64.getDecoder().decode(sessionKey);

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
        SecretKey key = new javax.crypto.spec.SecretKeySpec(sessionKeyBytes, "AES");

        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

        byte[] decryptedData = cipher.doFinal(encryptedDataBytes);
        var str = new String(decryptedData, "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 将 JSON 字符串解析为 Map
            Map<String, Object> resultMap = objectMapper.readValue(decryptedData, Map.class);

            // 创建一个新的 map
            var map = new HashMap<String, String>();

            // 从 resultMap 中获取所需字段并放入新的 map
            map.put("phoneNumber", (String) resultMap.get("phoneNumber"));
            map.put("purePhoneNumber", (String) resultMap.get("purePhoneNumber"));
            map.put("countryCode", (String) resultMap.get("countryCode"));
            return map;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("解析解密数据失败: " + e.getMessage());
        }
    }

    // 解密用户信息并获取手机号
    public Mono<Map<String, String>> getEncryptedPhoneNumber(String unionId, String encryptedData, String iv) {
        return redisService.get(getSessionMapKey(unionId)).flatMap((sessionKey) -> {
            try {
                var re = decryptPhoneNumberInternal(encryptedData, iv, sessionKey);
                return Mono.just(re);
            } catch (Exception ex) {
                ex.printStackTrace();
                return Mono.error(ex);
            }
        }).switchIfEmpty(Mono.defer(() -> {
            return Mono.error(new Exception("sessionKey过期，或者不合法")); // 或者返回一个默认值
        }));


    }


}
