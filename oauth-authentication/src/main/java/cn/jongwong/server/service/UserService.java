package cn.jongwong.server.service;

import cn.jongwong.server.dto.user.UserRes;
import cn.jongwong.server.entity.User;
import cn.jongwong.server.util.response.Page;
import reactor.core.publisher.Mono;

public interface UserService {

    // 根据标识符查询用户
    Mono<User> getUserByIdentifier(String identifier);

    // 根据手机号查询用户
    Mono<User> getUserByMobileNumber(String mobile);

    // 创建新用户
    Mono<User> createUser(User user);

    // 更新用户信息
    Mono<User> updateUser(User user);


    // 删除用户
    Mono<Void> deleteUser(Long userId);


    Mono<Page<UserRes>> getUsersList(String username, String email, int page, int size);

}
