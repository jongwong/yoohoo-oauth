package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.GroupAdminVO;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface GroupAdminRepository extends GenericReactiveRepository<GroupAdminVO, String> {

    // 根据 userId 查询管理员
    Flux<GroupAdminVO> findByUserId(String userId);
}
