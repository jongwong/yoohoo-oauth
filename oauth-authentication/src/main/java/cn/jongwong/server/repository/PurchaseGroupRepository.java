package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.PurchaseGroupVO;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseGroupRepository extends GenericReactiveRepository<PurchaseGroupVO, String> {
}
