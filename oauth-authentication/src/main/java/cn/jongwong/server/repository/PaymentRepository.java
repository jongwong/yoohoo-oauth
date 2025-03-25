package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.PaymentVO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface PaymentRepository extends GenericReactiveRepository<PaymentVO, String> {

    // 根据支付ID查找支付记录
    Mono<PaymentVO> findById(String paymentId);

    // 根据订单ID查找支付记录
    Mono<PaymentVO> findByOrderId(String orderId);

    @Query("SELECT * FROM tb_payment WHERE order_id IN (:orderIds)")
    Flux<PaymentVO> findByOrderIdIn(List<String> orderIds);
}
