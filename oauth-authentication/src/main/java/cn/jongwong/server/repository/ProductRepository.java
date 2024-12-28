package cn.jongwong.server.repository;

import cn.jongwong.server.entity.ProductVO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends R2dbcRepository<ProductVO, String> {

    // 使用自定义 SQL 查询获取 code 最大的商品
    @Query("SELECT * FROM products ORDER BY code DESC LIMIT 1")
    Mono<ProductVO> findProductWithMaxCode();
}
