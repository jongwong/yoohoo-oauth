package cn.jongwong.server.repository;


import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.UserCouponsVO;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserCouponsRepository extends GenericReactiveRepository<UserCouponsVO, String> {
    Mono<UserCouponsVO> findByCouponsId(String couponsId);

    Flux<UserCouponsVO> findByUserId(String userId);
}
