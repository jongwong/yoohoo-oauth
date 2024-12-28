package cn.jongwong.server.repository;

import cn.jongwong.server.entity.UserVO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<UserVO, Long> {

    // 根据标识符查找用户（支持手机号、用户名、邮箱或 ID）
    @Query("SELECT * FROM tb_user WHERE id = :identifier OR username = :identifier OR mobile = :identifier OR email = :identifier")
    Mono<UserVO> findByIdentifier(String identifier);

    // 根据手机号查找用户
    Mono<UserVO> findByMobile(String mobile);


}
