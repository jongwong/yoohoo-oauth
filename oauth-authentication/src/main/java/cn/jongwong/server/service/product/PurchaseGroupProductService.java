package cn.jongwong.server.service.product;

import cn.jongwong.server.entity.PurchaseGroupProductVO;
import cn.jongwong.server.repository.PurchaseGroupProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PurchaseGroupProductService {

    private final PurchaseGroupProductRepository purchaseGroupProductRepository;

    @Autowired
    public PurchaseGroupProductService(PurchaseGroupProductRepository purchaseGroupProductRepository) {
        this.purchaseGroupProductRepository = purchaseGroupProductRepository;
    }

    /**
     * 根据团购ID查询团购商品信息
     *
     * @param purchaseGroupId 团购ID
     * @return Mono<PurchaseGroupProductVO> 单个团购商品信息
     */
    public Mono<PurchaseGroupProductVO> findByPurchaseGroupId(String purchaseGroupId) {
        return purchaseGroupProductRepository.findById(purchaseGroupId);
    }

    /**
     * 保存团购商品信息
     *
     * @param purchaseGroupProductVO 团购商品实体
     * @return Mono<PurchaseGroupProductVO> 保存后的团购商品信息
     */
    public Mono<PurchaseGroupProductVO> save(PurchaseGroupProductVO purchaseGroupProductVO) {
        return purchaseGroupProductRepository.save(purchaseGroupProductVO);
    }

    /**
     * 删除团购商品信息
     *
     * @param id 团购商品ID
     * @return Mono<Void> 操作结果
     */
    public Mono<Void> deleteById(String id) {
        return purchaseGroupProductRepository.deleteById(id);
    }
}
