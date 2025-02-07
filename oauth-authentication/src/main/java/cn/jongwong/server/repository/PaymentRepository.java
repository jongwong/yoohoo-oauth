package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.PaymentVO;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PaymentRepository extends GenericReactiveRepository<PaymentVO, String> {

    // 根据支付ID查找支付记录
    Mono<PaymentVO> findById(String paymentId);

    // 根据订单ID查找支付记录
    Mono<PaymentVO> findByOrderId(String orderId);
}
