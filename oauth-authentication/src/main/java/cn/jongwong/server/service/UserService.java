package cn.jongwong.server.service;

import cn.jongwong.server.dto.user.CurrentAuthenticationUserRO;
import cn.jongwong.server.dto.user.UserRO;
import cn.jongwong.server.entity.UserVO;
import cn.jongwong.server.util.response.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {

    // 根据标识符查询用户
    Mono<UserRO> getUserByIdentifier(String identifier);

    // 根据标识符查询用户
    Mono<UserVO> getUserWithPasswordByIdentifier(String identifier);


    // 根据手机号查询用户
    Mono<UserRO> getUserByMobileNumber(String mobile);

    // 根据手机号查询用户
    Mono<UserVO> getUserWithPasswordByMobileNumber(String mobile);


    // 创建新用户
    Mono<UserRO> createUser(UserVO userVO);

    // 更新用户信息
    Mono<UserRO> updateUser(UserVO userVO);

    // 更新用户信息
    Mono<UserRO> updateMergeUser(UserVO userVO);


    // 删除用户
    Mono<Void> deleteUser(String userId);


    Mono<Page<UserRO>> getUsersList(String username, String email, int page, int size);

    Mono<String> getCurrentUserReactiveId();

    Mono<CurrentAuthenticationUserRO> getCurrentUserReactive();

    CurrentAuthenticationUserRO getCurrentUser();


    Mono<UserRO> findById(String id);

    Flux<UserRO> findAllByIds(List<String> ids);

}
