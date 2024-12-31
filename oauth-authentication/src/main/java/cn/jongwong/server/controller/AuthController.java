package cn.jongwong.server.controller;

import cn.jongwong.server.config.security.jwt.JwtUtil;
import cn.jongwong.server.dto.WeChatLoginDTO;
import cn.jongwong.server.dto.WeChatPhoneDecryptDTO;
import cn.jongwong.server.service.ThirdPartyLoginService;
import cn.jongwong.server.service.UserService;
import cn.jongwong.server.service.WeChatAuthService;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RestController
public class AuthController {
    @Autowired
    WeChatAuthService weChatAuthService;


    @Autowired
    private UserService userService; // 用于验证用户名和密码

    @Autowired
    private JwtUtil jwtUtil; // 用于生成 JWT

    @Autowired
    ThirdPartyLoginService thirdPartyLoginService;

    // 登录接口
    @PostMapping("/client/wechat/openid")
    public Mono<Response<Map<String, String>>> login(@RequestBody WeChatLoginDTO request) {
        return weChatAuthService.wxLogin(request.getCode())
                .flatMap(map -> {
                    if (map == null || map.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("Response map is empty"));
                    }
                    String openid = map.get("openid");
                    System.out.printf("-------openid-------%s%n", openid);
                    if (openid == null || openid.isBlank()) {
                        return Mono.error(new IllegalArgumentException("OpenID not found in response"));
                    }
                    // 查询第三方登录信息
                    return thirdPartyLoginService.findById(openid)
                            .flatMap(t -> {
                                System.out.printf("-------t.getUserId()-------%s%n", t.getUserId());
                                // 将第三方登录信息合并到返回的 map 中
                                map.put("userId", t.getUserId());
                                map.put("provider", String.valueOf(t.getProvider())); // 第三方平台
                                return Mono.just(map);
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                // 如果没有绑定用户，也可返回部分 weChatAuthService 数据
                                map.put("userId", null); // 未绑定用户
                                return Mono.just(map);
                            })).onErrorResume(throwable -> Mono.just(map));
                })
                .map(Response::success)
                .onErrorResume(Response::error);
    }

    // 解密手机号接口
    @PostMapping("/client/wechat/decrypt-phone")
    public Mono<String> decryptPhoneNumber(@RequestBody WeChatPhoneDecryptDTO request) {
        return weChatAuthService.getUserInfoAndPhoneNumber(request.getEncryptedData(), request.getIv(), request.getSessionKey());
    }

    @PostMapping("/auth/token")
    public Mono<Response<String>> login(ServerWebExchange exchange) {
        // 从请求中解析表单数据
        return exchange.getFormData()
                .flatMap(formData -> {
                    String grantType = formData.getFirst("grant_type");
                    String username = formData.getFirst("username");
                    String password = formData.getFirst("password");

                    System.out.printf("grantType: %s, username: %s, password: %s%n", grantType, username, password);

                    if ("password".equals(grantType)) {
                        // 处理 password 授权类型
                        return userService.getUserByIdentifier(username)
                                .flatMap(user -> {
                                    if (user == null) {
                                        return Mono.error(new IllegalArgumentException("User not found"));
                                    }
                                    // 校验密码，假设你已经有密码加密和验证的逻辑
//                                    if (!passwordEncoder.matches(password, user.getPassword())) {
//                                        return Mono.error(new IllegalArgumentException("Invalid password"));
//                                    }

                                    // 生成 JWT token
                                    String jwtToken = jwtUtil.generateToken(user);

                                    // 将 JWT 存储到 session
                                    return exchange.getSession()
                                            .doOnNext(session -> session.getAttributes().put("JWT", jwtToken))
                                            .then(Mono.defer(() -> {
                                                // 设置 JWT 为 HttpOnly Cookie
                                                exchange.getResponse().addCookie(ResponseCookie.from("JWT", jwtToken)
                                                        .httpOnly(true) // 防止客户端 JavaScript 访问
                                                        .secure(true)    // 在 HTTPS 下传输
                                                        .path("/")       // 设置路径为根，保证在所有路径下都能访问
                                                        .maxAge(Duration.ofHours(1)) // 设置过期时间
                                                        .build());
                                                return Mono.just(Response.success(jwtToken));
                                            }));
                                });
                    }

                    return Mono.just(Response.error("Unsupported grant type"));
                });
    }
}
