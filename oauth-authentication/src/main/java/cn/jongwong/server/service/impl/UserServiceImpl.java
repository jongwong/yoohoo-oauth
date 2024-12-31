package cn.jongwong.server.service.impl;

import cn.jongwong.server.common.MapperUtil;
import cn.jongwong.server.common.QueryBuilder;
import cn.jongwong.server.dto.user.UserRO;
import cn.jongwong.server.entity.UserVO;
import cn.jongwong.server.repository.UserRepository;
import cn.jongwong.server.service.UserService;
import cn.jongwong.server.util.response.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public Mono<UserVO> getUserByIdentifier(String identifier) {
        return userRepository.findByIdentifier(identifier);
    }

    @Override
    public Mono<UserVO> getUserByMobileNumber(String mobile) {
        return userRepository.findByMobile(mobile);
    }

    @Override
    public Mono<UserVO> createUser(UserVO userVO) {
        return userRepository.save(userVO);
    }

    @Override
    public Mono<UserVO> updateUser(UserVO userVO) {
        return userRepository.save(userVO);  // 如果存在同样的 ID，会执行更新操作
    }


    @Override
    public Mono<Void> deleteUser(Long userId) {
        return userRepository.deleteById(userId);
    }

    public Mono<Page<UserRO>> getUsersList(String username, String email, int page, int size) {


        return new QueryBuilder<>(r2dbcEntityTemplate, UserVO.class)
                .addLikeCondition("username", username)
                .addEqualCondition("username", username)
                .addEqualCondition("email", email)
                .paginate(page, size)
                .exec().map(userPage -> {
                    // 转换 User -> UserRes
                    List<UserRO> userROList = userPage.getData().stream()
                            .map(user -> MapperUtil.mapFields(user, UserRO.class))
                            .toList();

                    // 构建新的 Page<UserRes>
                    return new Page<>(
                            userROList,
                            userPage.getTotal(),
                            userPage.getPage(),
                            userPage.getSize()
                    );
                });
    }


    /**
     * 获取当前用户的 ID。
     *
     * @return 当前用户的 ID，如果没有认证的用户则返回 "系统用户"
     */
    public Mono<String> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return Mono.just(userDetails.getUsername());  // 返回用户的用户名作为 ID
        }

        return Mono.just("系统用户");  // 如果没有认证的用户，返回一个默认值
    }


}
