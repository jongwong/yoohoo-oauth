package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.OrderItemVO;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OrderItemRepository extends GenericReactiveRepository<OrderItemVO, String> {


    // 根据支付ID查找订单明细
    Flux<OrderItemVO> findByOrderId(String orderId);
}
