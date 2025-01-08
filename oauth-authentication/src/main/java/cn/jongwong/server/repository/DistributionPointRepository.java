package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.DistributionPointVO;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributionPointRepository extends GenericReactiveRepository<DistributionPointVO, String> {


}