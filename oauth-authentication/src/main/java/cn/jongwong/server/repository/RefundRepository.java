package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.RefundVO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface RefundRepository extends GenericReactiveRepository<RefundVO, String> {

    // 根据支付ID查找支付记录
    Mono<RefundVO> findById(String id);

    // 根据订单ID查找支付记录
    Mono<RefundVO> findByOrderId(String orderId);

    @Query("SELECT * FROM tb_refund WHERE order_id IN (:orderIds)")
    Flux<RefundVO> findByOrderIdIn(List<String> orderIds);

}
