package cn.jongwong.server.controller;

import cn.jongwong.server.common.MapperUtil;
import cn.jongwong.server.config.security.jwt.JwtUtil;
import cn.jongwong.server.dto.WeChatLoginDTO;
import cn.jongwong.server.dto.WeChatRegistrationDTO;
import cn.jongwong.server.dto.user.UserRO;
import cn.jongwong.server.entity.UserVO;
import cn.jongwong.server.service.ThirdPartyLoginService;
import cn.jongwong.server.service.UserService;
import cn.jongwong.server.service.WeChatAuthService;
import cn.jongwong.server.util.response.Response;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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

    // 使用 Redis 存储调用次数，假设有一个 RedisService 可用于操作 Redis
    @Autowired
    private RedisService redisService;


    // 临时 token 的有效期（单位：秒）
    private static final int TOKEN_EXPIRATION = 3600; // 1小时
    private static final int MAX_CALL_COUNT = 20; // 最大调用次数

    @PostMapping("/client/wechat/login")
    public Mono<Response<Map<String, String>>> login(@RequestBody WeChatLoginDTO request) {

        return weChatAuthService.wxLogin(request.getCode())
                .map(Response::success)
                .onErrorResume(e -> Mono.just(Response.error("请求失败: " + e.getMessage())));
    }

    @PostMapping("/client/wechat/register")
    public Mono<Response<UserRO>> register(@Valid @RequestBody WeChatRegistrationDTO request) {
        // 如果调用次数没有超过限制，则调用解密方法
        return weChatAuthService.getEncryptedPhoneNumber(request.getUnionId(), request.getEncryptedData(), request.getIv()).flatMap((e) -> {

            var mobile = e.get("phoneNumber");
            return thirdPartyLoginService.findWithPasswordUserByThirdPartyUserId(request.getUnionId()).map(u -> {
                var newU = MapperUtil.mapFields(u, UserVO.class);
                newU.setName(request.getName());
                newU.setNickname(request.getNickname());
                newU.setMobile(mobile);
                return newU;
            });

        }).flatMap((u) -> userService.updateUser(u)).map(Response::success);
    }


    @PostMapping("/auth/token")
    public Mono<Response<String>> login(ServerWebExchange exchange) {
        // 从请求中解析表单数据
        return exchange.getFormData()
                .flatMap(formData -> {
                    String grantType = formData.getFirst("grant_type");
                    String username = formData.getFirst("username");
                    String password = formData.getFirst("password");

                    if ("password".equals(grantType)) {
                        // 处理 password 授权类型
                        return userService.getUserByIdentifier(username)
                                .map(user -> {
                                    // 生成 JWT token
                                    return jwtUtil.generateToken(user, false);
                                }).map(Response::success);
                    }

                    return Mono.just(Response.error("Unsupported grant type"));
                });
    }
}
