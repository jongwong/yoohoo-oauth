package cn.jongwong.server.service;

import cn.jongwong.server.entity.DistributionPointProductVO;
import cn.jongwong.server.repository.DistributionPointProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DistributionPointProductService {

    private final DistributionPointProductRepository distributionPointProductRepository;

    // 删除配送点和商品关联
    public Mono<Void> delete(String id) {
        return distributionPointProductRepository.deleteById(id);
    }

    // 根据ID查询配送点商品关联
    public Mono<DistributionPointProductVO> findById(String id) {
        return distributionPointProductRepository.findById(id);
    }

    // 查询所有配送点商品关联
    public Flux<DistributionPointProductVO> listAll() {
        return distributionPointProductRepository.findAll();
    }

    // 更新配送点商品关联
    public Mono<DistributionPointProductVO> update(DistributionPointProductVO distributionPointProductVO) {
        return distributionPointProductRepository.save(distributionPointProductVO);
    }

    // 新增配送点商品关联
    public Mono<DistributionPointProductVO> create(DistributionPointProductVO distributionPointProductVO) {
        return distributionPointProductRepository.save(distributionPointProductVO);
    }
}
