package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.MiniAppMenuVO;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MiniAppMenuRepository extends GenericReactiveRepository<MiniAppMenuVO, String> {

    // 根据类别ID查询菜单
    Flux<MiniAppMenuVO> findByCategoryId(String categoryId);

    // 根据排序字段查询菜单
    Flux<MiniAppMenuVO> findBySort(Integer sort);

    // 获取菜单的所有项，按排序字段排序
    Flux<MiniAppMenuVO> findAllByOrderBySortAsc();
}
