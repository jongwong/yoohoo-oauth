package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.ProductImageVO;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface ProductImageRepository extends GenericReactiveRepository<ProductImageVO, String> {

    Flux<ProductImageVO> findByProductId(String productId);


    // 删除某个商品的所有图片
    Mono<Void> deleteAllByProductId(String productId);

    Flux<ProductImageVO> findAllByProductIdIn(List<String> ids);

    // 自定义查询，根据 productId 和 url 查找图片
    Mono<ProductImageVO> findByProductIdAndUrl(String productId, String url);
}
