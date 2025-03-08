package cn.jongwong.server.service;

import cn.jongwong.server.common.AutoCreatedField;
import cn.jongwong.server.common.AutoUpdatedField;
import cn.jongwong.server.entity.PaymentVO;
import cn.jongwong.server.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    Mono<PaymentVO> update(@AutoUpdatedField PaymentVO data) {
        return paymentRepository.save(data);
    }

    public Mono<PaymentVO> insert(@AutoCreatedField @AutoUpdatedField PaymentVO data) {
        return paymentRepository.insert(data);
    }


    Mono<PaymentVO> findOneById(String id) {
        return paymentRepository.findById(id);
    }

    Mono<PaymentVO> findByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}
