package cn.jongwong.server.service.impl;

import cn.jongwong.server.common.MapperUtil;
import cn.jongwong.server.common.QueryBuilder;
import cn.jongwong.server.dto.user.UserRes;
import cn.jongwong.server.entity.User;
import cn.jongwong.server.repository.UserRepository;
import cn.jongwong.server.service.UserService;
import cn.jongwong.server.util.response.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    R2dbcEntityTemplate r2dbcEntityTemplate;


    @Autowired
    private UserRepository userRepository;

    @Override
    public Mono<User> getUserByIdentifier(String identifier) {
        return userRepository.findByIdentifier(identifier);
    }

    @Override
    public Mono<User> getUserByMobileNumber(String mobile) {
        return userRepository.findByMobile(mobile);
    }

    @Override
    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Mono<User> updateUser(User user) {
        return userRepository.save(user);  // 如果存在同样的 ID，会执行更新操作
    }


    @Override
    public Mono<Void> deleteUser(Long userId) {
        return userRepository.deleteById(userId);
    }

    public Mono<Page<UserRes>> getUsersList(String username, String email, int page, int size) {


        return new QueryBuilder<>(r2dbcEntityTemplate, User.class).addLikeCondition("username", username)
                .addEqualCondition("username", username)
                .addEqualCondition("email", email)
                .executeQuery(page, size).map(userPage -> {
                    // 转换 User -> UserRes
                    List<UserRes> userResList = userPage.getData().stream()
                            .map(user -> MapperUtil.mapFields(user, UserRes.class))
                            .toList();

                    // 构建新的 Page<UserRes>
                    return new Page<>(
                            userResList,
                            userPage.getTotal(),
                            userPage.getPage(),
                            userPage.getSize()
                    );
                });
    }


}
