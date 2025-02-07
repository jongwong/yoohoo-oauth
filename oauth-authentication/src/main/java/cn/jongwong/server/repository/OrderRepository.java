package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.OrderVO;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends GenericReactiveRepository<OrderVO, String> {

    // 根据订单ID查找订单
    Mono<OrderVO> findById(String orderId);


}
