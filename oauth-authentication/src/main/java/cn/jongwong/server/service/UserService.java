package cn.jongwong.server.service;

import cn.jongwong.server.entity.User;
import reactor.core.publisher.Mono;

public interface UserService {

    // 根据标识符查询用户
    Mono<User> getUserByIdentifier(String identifier);

    // 根据手机号查询用户
    Mono<User> getUserByMobileNumber(String mobile);
}
