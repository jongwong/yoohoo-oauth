package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.ProductCategoryVO;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends GenericReactiveRepository<ProductCategoryVO, String> {


}
