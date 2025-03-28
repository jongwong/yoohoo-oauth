package cn.jongwong.server.service;

import cn.jongwong.server.entity.RefundVO;
import cn.jongwong.server.repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RefundService {

    @Autowired
    private RefundRepository refundRepository;


    @Autowired
    private UserService userService;

    Mono<RefundVO> update(RefundVO data) {
        return userService.getCurrentUserReactive().map((u) -> {
            data.setUpdatedBy(u.getId());
            data.setUpdatedByName(u.getName());
            data.setUpdatedAt(LocalDateTime.now());
            return data;
        }).flatMap((e) -> refundRepository.save(e)).flatMap((d) -> refundRepository.findById(d.getId()));
    }


    Mono<RefundVO> save(RefundVO data) {

        if (data.getOrderId() == null) {
            System.out.printf("=============111===========%s%n", 111);
            return insert(data);
        }
        return refundRepository.existsById(data.getOrderId()).flatMap((exists) -> {
            System.out.printf("=============exists===========%s%n", exists);
            if (exists) {
                return update(data);
            } else {
                return insert(data);
            }
        });
    }

    public Mono<RefundVO> insert(RefundVO data) {
        return userService.getCurrentUserReactive().map((u) -> {
            data.setId(UUID.randomUUID().toString());
            data.setCreatedBy(u.getId());
            data.setCreatedByName(u.getName());
            data.setCreatedAt(LocalDateTime.now());
            return data;
        }).flatMap((u) -> refundRepository.insert(data)).flatMap((d) -> refundRepository.findById(d.getId()));
    }


    Mono<RefundVO> findOneById(String id) {
        return refundRepository.findById(id);
    }

    Mono<RefundVO> findOneByOrderId(String orderId) {
        return refundRepository.findByOrderId(orderId);
    }

    Mono<Boolean> existsById(String orderId) {
        return refundRepository.existsById(orderId);
    }

    Flux<RefundVO> findByOrderIdIn(List<String> orderIds) {
        return refundRepository.findByOrderIdIn(orderIds);
    }



}
