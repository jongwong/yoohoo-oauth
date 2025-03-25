package cn.jongwong.server.controller.client;


import cn.jongwong.server.dto.user.UserRO;
import cn.jongwong.server.entity.UserCouponsRO;
import cn.jongwong.server.service.UserCouponsService;
import cn.jongwong.server.service.UserService;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/client/user")
public class UserInfoController {

    @Autowired
    private UserCouponsService userCouponsService;

    @Autowired
    private UserService userService;


    // 获取最近的配送点
    @GetMapping("/coupons")
    public Mono<Response<List<UserCouponsRO>>> getUserCoupons() {

        return userService.getCurrentUserReactive()
                .flatMap(u -> userCouponsService.getUserCouponsByUserId(u.getId()).collectList()).map(Response::ok);

    }

    // 根据标识符查询用户
    @GetMapping("/{identifier}")
    public Mono<Response<UserRO>> getUserByIdentifier(@PathVariable String identifier) {
        return userService.getUserByIdentifier(identifier).map(Response::success);
    }
}
