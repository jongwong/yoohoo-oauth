package cn.jongwong.server.service;

import cn.jongwong.server.entity.MiniAppMenuVO;
import cn.jongwong.server.repository.MiniAppMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MiniAppMenuService {

    private final MiniAppMenuRepository miniAppMenuRepository;

    // 获取所有菜单项，按排序字段升序
    public Flux<MiniAppMenuVO> getAllMenus() {
        return miniAppMenuRepository.findAllByOrderBySortAsc();
    }

    // 根据类别ID获取菜单
    public Flux<MiniAppMenuVO> getMenusByCategory(String categoryId) {
        return miniAppMenuRepository.findByCategoryId(categoryId);
    }

    // 根据ID获取菜单项
    public Mono<MiniAppMenuVO> getMenuById(String id) {
        return miniAppMenuRepository.findById(id);
    }

    // 根据排序字段获取菜单项
    public Flux<MiniAppMenuVO> getMenusBySort(Integer sort) {
        return miniAppMenuRepository.findBySort(sort);
    }

    // 添加菜单项
    public Mono<MiniAppMenuVO> addMenu(MiniAppMenuVO menuVO) {
        return miniAppMenuRepository.save(menuVO);
    }

    // 更新菜单项
    public Mono<MiniAppMenuVO> updateMenu(MiniAppMenuVO menuVO) {
        return miniAppMenuRepository.save(menuVO);
    }

    // 删除菜单项
    public Mono<Void> deleteMenu(String id) {
        return miniAppMenuRepository.deleteById(id);
    }
}
