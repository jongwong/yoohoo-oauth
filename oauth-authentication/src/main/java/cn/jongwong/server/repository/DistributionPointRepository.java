package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.DistributionPointVO;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface DistributionPointRepository extends GenericReactiveRepository<DistributionPointVO, String> {

    public Flux<DistributionPointVO> findAllByIdIn(List<String> id);
}