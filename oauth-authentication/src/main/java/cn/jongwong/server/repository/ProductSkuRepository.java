package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.ProductSkuVO;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductSkuRepository extends GenericReactiveRepository<ProductSkuVO, String> {

    // 查询某个商品的所有 SKU
    Flux<ProductSkuVO> findAllByProductId(String productId);


    // 查询某个 SKU 的详情
    @NotNull
    Mono<ProductSkuVO> findById(String id);

    // 通过 SKU 名称模糊查询
    Flux<ProductSkuVO> findByNameContaining(String name);


    // 1️⃣ 删除指定商品的所有 SKU
    Mono<Void> deleteAllByProductId(String productId);


}
