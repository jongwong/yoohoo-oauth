package cn.jongwong.server.repository;

import cn.jongwong.server.entity.ProductImageVO;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductImageRepository extends R2dbcRepository<ProductImageVO, String> {

    Flux<ProductImageVO> findByProductId(String productId);


    // 删除某个商品的所有图片
    Mono<Void> deleteAllByProductId(String productId);


    // 自定义查询，根据 productId 和 url 查找图片
    Mono<ProductImageVO> findByProductIdAndUrl(String productId, String url);
}
