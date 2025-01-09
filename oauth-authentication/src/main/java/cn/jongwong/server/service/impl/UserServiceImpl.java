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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    R2dbcEntityTemplate r2dbcEntityTemplate;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserRO> getUserByIdentifier(String identifier) {
        return userRepository.findByIdentifier(identifier).map((e) -> MapperUtil.mapFields(e, UserRO.class));

    }

    @Override
    public Mono<UserVO> getUserWithPasswordByIdentifier(String identifier) {
        return userRepository.findByIdentifier(identifier);
    }

    @Override
    public Mono<UserRO> getUserByMobileNumber(String mobile) {
        return userRepository.findByMobile(mobile).map((e) -> MapperUtil.mapFields(e, UserRO.class));
    }

    @Override
    public Mono<UserVO> getUserWithPasswordByMobileNumber(String mobile) {
        return null;
    }

    @Override
    public Mono<UserRO> createUser(UserVO userVO) {
        // 检查未设置的字段并设置默认值
        if (userVO.getId() == null) {
            userVO.setId(UUID.randomUUID().toString());
        }
        if (userVO.getCreatedAt() == null) {
            userVO.setCreatedAt(LocalDateTime.now());
        }
        if (userVO.getUpdatedAt() == null) {
            userVO.setUpdatedAt(LocalDateTime.now());
        }
        if (userVO.getExpired() == 0) {
            userVO.setExpired(0); // 默认未过期
        }
        if (userVO.getLocked() == 0) {
            userVO.setLocked(0); // 默认未锁定
        }
        if (userVO.getEnabled() == 0) {
            userVO.setEnabled(1); // 默认启用
        }
        if (userVO.getAuthorities() == null) {
            userVO.setAuthorities(new String[]{"ROLE_USER"}); // 默认角色
        }
        if (userVO.getName() == null) {
            userVO.setName(""); // 默认角色
        }


        // 插入到数据库
        return userRepository.insert(userVO).map(e -> MapperUtil.mapFields(e, UserRO.class));
    }

    @Override
    public Mono<UserRO> updateUser(UserVO userVO) {
        return userRepository.save(userVO).map((e) -> MapperUtil.mapFields(e, UserRO.class));  // 如果存在同样的 ID，会执行更新操作
    }

    @Override
    public Mono<UserRO> updateMergeUser(UserVO userVO) {
        return userRepository.findById(userVO.getId())
                .flatMap(existingUser -> {

                    MapperUtil.merge(existingUser, userVO);
                    // 继续增加其他字段的判断
                    return userRepository.save(existingUser);
                }).map((e) -> MapperUtil.mapFields(e, UserRO.class));
    }


    @Override
    public Mono<Void> deleteUser(String userId) {
        return userRepository.deleteById(userId);
    }

    public Mono<Page<UserRO>> getUsersList(String name, String mobile, int page, int size) {


        return new QueryBuilder<>(r2dbcEntityTemplate, UserVO.class)
                .addLikeCondition("name", name)
                .addEqualCondition("mobile", mobile)
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
     * @deprecated
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

    public Mono<UserRO> findById(String id) {
        return userRepository.findById(id).map((e) -> MapperUtil.mapFields(e, UserRO.class));
    }

}
