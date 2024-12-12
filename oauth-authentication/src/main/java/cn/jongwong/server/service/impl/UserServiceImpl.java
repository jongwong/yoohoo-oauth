package cn.jongwong.server.service.impl;

import cn.jongwong.server.entity.User;
import cn.jongwong.server.repository.UserRepository;
import cn.jongwong.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户标识获取用户信息
     * 标识可以是：用户ID、用户名、手机号码、邮箱等
     *
     * @param identifier 用户标识
     * @return Mono<User> 对象
     */
    @Override
    public Mono<User> getUserByIdentifier(String identifier) {
        Mono<User> user = userRepository.findByIdentifier(identifier);
        return user;
    }

    @Override
    public Mono<User> getUserByMobileNumber(String mobile) {
        // 调用repository层的方法根据手机号查询用户
        return userRepository.findByMobile(mobile);
    }
}