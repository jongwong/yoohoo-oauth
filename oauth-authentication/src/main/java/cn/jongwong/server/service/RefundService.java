package cn.jongwong.server.service;

import cn.jongwong.server.entity.RefundVO;
import cn.jongwong.server.repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

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
        }).flatMap((d) -> refundRepository.save(d)).flatMap((d) -> refundRepository.findById(d.getId()));
    }

    public Mono<RefundVO> insert(RefundVO data) {
        return userService.getCurrentUserReactive().map((u) -> {
            data.setCreatedBy(u.getId());
            data.setCreatedByName(u.getName());
            data.setCreatedAt(LocalDateTime.now());
            return data;
        }).flatMap((u) -> refundRepository.insert(data)).flatMap((d) -> refundRepository.findById(d.getId()));
    }


    Mono<RefundVO> findOneById(String id) {
        return refundRepository.findById(id);
    }

    Mono<RefundVO> findByOrderId(String orderId) {
        return refundRepository.findByOrderId(orderId);
    }

    Mono<Boolean> existsById(String orderId) {
        return refundRepository.existsById(orderId);
    }



}
