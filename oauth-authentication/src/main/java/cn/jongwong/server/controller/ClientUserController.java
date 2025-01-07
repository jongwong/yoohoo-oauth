package cn.jongwong.server.controller;

import cn.jongwong.server.dto.user.UserRO;
import cn.jongwong.server.service.UserService;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client/user")
public class ClientUserController {

    @Autowired
    private UserService userService;

    // 根据标识符查询用户
    @GetMapping("/{identifier}")
    public Mono<Response<UserRO>> getUserByIdentifier(@PathVariable String identifier) {
        return userService.getUserByIdentifier(identifier).map(Response::success);
    }


}
