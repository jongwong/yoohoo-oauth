package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.ProductCategoryVO;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends GenericReactiveRepository<ProductCategoryVO, String> {

    Flux<ProductCategoryVO> findAllByIdIn(List<String> ids);
}
