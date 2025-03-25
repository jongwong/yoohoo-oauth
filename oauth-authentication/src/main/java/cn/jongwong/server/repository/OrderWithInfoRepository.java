package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.OrderWithInfoVO;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface OrderWithInfoRepository extends GenericReactiveRepository<OrderWithInfoVO, String> {

    // 根据订单ID查找订单
    Mono<OrderWithInfoVO> findById(String orderId);

    // 根据订单ID查找订单
    Mono<OrderWithInfoVO> findByNum(String num);


}
