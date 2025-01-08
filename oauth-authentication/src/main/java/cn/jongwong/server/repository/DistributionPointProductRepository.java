package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.DistributionPointProductVO;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface DistributionPointProductRepository extends GenericReactiveRepository<DistributionPointProductVO, String> {

    // 根据配送点ID查询所有关联商品
    Flux<DistributionPointProductVO> findByPointId(String pointId);

    // 根据商品ID查询所有关联的配送点
    Flux<DistributionPointProductVO> findByProductId(String productId);
}