package cn.jongwong.server.controller;

import cn.jongwong.server.dto.user.UserRes;
import cn.jongwong.server.entity.User;
import cn.jongwong.server.service.UserService;
import cn.jongwong.server.util.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 根据标识符查询用户
    @GetMapping("/{identifier}")
    public Mono<User> getUserByIdentifier(@PathVariable String identifier) {
        return userService.getUserByIdentifier(identifier);
    }

    // 根据手机号查询用户
    @GetMapping("/mobile/{mobile}")
    public Mono<User> getUserByMobile(@PathVariable String mobile) {
        return userService.getUserByMobileNumber(mobile);
    }

    // 创建用户
    @PostMapping
    public Mono<User> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping
    public Mono<PageResponse<UserRes>> getUserList(@RequestParam(required = false) String username,
                                                   @RequestParam(required = false) String email,
                                                   @RequestParam(required = true) int page,
                                                   @RequestParam(required = true) int size) {
        return PageResponse.reactivePageSuccess(userService.getUsersList(username, email, page, size));
    }

    // 更新用户
    @PutMapping("/{id}")
    public Mono<User> updateUser(@PathVariable String id, @RequestBody User user) {
        user.setId(id);
        return userService.updateUser(user);
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }


}
