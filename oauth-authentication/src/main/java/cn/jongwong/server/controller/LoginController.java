package cn.jongwong.server.controller;

import cn.jongwong.server.config.security.jwt.JwtUtil;
import cn.jongwong.server.service.UserService;
import cn.jongwong.server.util.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private UserService userService; // 用于验证用户名和密码

    @Autowired
    private JwtUtil jwtUtil; // 用于生成 JWT


    @PostMapping("/token")
    public Mono<ResponseResult<String>> login(ServerWebExchange exchange) {
        // 从请求中解析表单数据
        return exchange.getFormData()
                .flatMap(formData -> {
                    System.out.printf("-------formData-------%s%n", formData);
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
                                    System.out.printf("-------jwtToken-------%s%n", jwtToken);

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
                                                return Mono.just(ResponseResult.success(jwtToken));
                                            }));
                                });
                    }

                    return Mono.just(ResponseResult.error("Unsupported grant type"));
                });
    }

}
