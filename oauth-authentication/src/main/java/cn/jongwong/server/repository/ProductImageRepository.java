package cn.jongwong.server.repository;

import cn.jongwong.server.entity.ProductImage;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductImageRepository extends R2dbcRepository<ProductImage, String> {

    Flux<ProductImage> findByProductId(String productId);
}
