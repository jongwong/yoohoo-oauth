package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.CouponsVO;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CouponsRepository extends GenericReactiveRepository<CouponsVO, String> {


    // 根据状态查询所有优惠券
    Flux<CouponsVO> findByStatus(Integer status);

    // 获取最大的优惠券ID (如果有此需求)
    Mono<CouponsVO> findTopByOrderByIdDesc();


}
