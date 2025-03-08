package cn.jongwong.server.service;

import cn.jongwong.server.common.AutoCreatedField;
import cn.jongwong.server.common.AutoUpdatedField;
import cn.jongwong.server.entity.RefundVO;
import cn.jongwong.server.repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RefundService {

    @Autowired
    private RefundRepository refundRepository;

    Mono<RefundVO> update(@AutoUpdatedField RefundVO data) {
        return refundRepository.save(data);
    }

    public Mono<RefundVO> insert(@AutoCreatedField @AutoUpdatedField RefundVO data) {
        return refundRepository.insert(data);
    }


    Mono<RefundVO> findOneById(String id) {
        return refundRepository.findById(id);
    }

    Mono<RefundVO> findByOrderId(String orderId) {
        return refundRepository.findByOrderId(orderId);
    }
}
