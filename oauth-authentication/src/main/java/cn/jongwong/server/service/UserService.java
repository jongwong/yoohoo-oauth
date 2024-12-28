package cn.jongwong.server.service;

import cn.jongwong.server.dto.user.UserRO;
import cn.jongwong.server.entity.UserVO;
import cn.jongwong.server.util.response.Page;
import reactor.core.publisher.Mono;

public interface UserService {

    // 根据标识符查询用户
    Mono<UserVO> getUserByIdentifier(String identifier);

    // 根据手机号查询用户
    Mono<UserVO> getUserByMobileNumber(String mobile);

    // 创建新用户
    Mono<UserVO> createUser(UserVO userVO);

    // 更新用户信息
    Mono<UserVO> updateUser(UserVO userVO);


    // 删除用户
    Mono<Void> deleteUser(Long userId);


    Mono<Page<UserRO>> getUsersList(String username, String email, int page, int size);

    Mono<String> getCurrentUserId();
}
