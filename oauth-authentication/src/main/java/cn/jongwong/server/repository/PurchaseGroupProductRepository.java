package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.PurchaseGroupProductVO;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface PurchaseGroupProductRepository extends GenericReactiveRepository<PurchaseGroupProductVO, String> {
    /**
     * 根据团购ID查询关联的商品
     *
     * @param purchaseGroupId 团购ID
     * @return Mono<PurchaseGroupProductVO> 商品及最大库存信息
     */
    Mono<PurchaseGroupProductVO> findByPurchaseGroupId(String purchaseGroupId);


    /**
     * 根据团购ID查询所有关联的商品
     *
     * @param purchaseGroupId 团购ID
     * @return Flux<PurchaseGroupProductVO> 商品信息
     */
    Flux<PurchaseGroupProductVO> findAllByPurchaseGroupId(String purchaseGroupId);

    Flux<PurchaseGroupProductVO> findAllByPurchaseGroupIdIn(List<String> ids);


}
