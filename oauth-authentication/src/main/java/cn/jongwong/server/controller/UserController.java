package cn.jongwong.server.controller;

import cn.jongwong.server.dto.user.UserRO;
import cn.jongwong.server.entity.UserVO;
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
    public Mono<UserRO> getUserByIdentifier(@PathVariable String identifier) {
        return userService.getUserByIdentifier(identifier);
    }

    // 根据手机号查询用户
    @GetMapping("/mobile/{mobile}")
    public Mono<UserRO> getUserByMobile(@PathVariable String mobile) {
        return userService.getUserByMobileNumber(mobile);
    }

    // 创建用户
    @PostMapping
    public Mono<UserRO> createUser(@RequestBody UserVO userVO) {
        return userService.createUser(userVO);
    }

    @GetMapping
    public Mono<PageResponse<UserRO>> getUserList(@RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String mobile,
                                                  @RequestParam(required = true) int page,
                                                  @RequestParam(required = true) int size) {
        return PageResponse.reactivePageSuccess(userService.getUsersList(name, mobile, page, size));
    }

    // 更新用户
    @PutMapping("/{id}")
    public Mono<UserRO> updateUser(@PathVariable String id, @RequestBody UserVO userVO) {
        userVO.setId(id);
        return userService.updateUser(userVO);
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id);
    }


}
