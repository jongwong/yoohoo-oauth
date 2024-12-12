package cn.jongwong.server.repository;

import cn.jongwong.server.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    // 根据标识符查找用户（支持手机号、用户名、邮箱或 ID）
    @Query("SELECT * FROM tb_user WHERE id = :identifier OR username = :identifier OR mobile = :identifier OR email = :identifier")
    Mono<User> findByIdentifier(String identifier);

    // 根据手机号查找用户
    Mono<User> findByMobile(String mobile);

}
