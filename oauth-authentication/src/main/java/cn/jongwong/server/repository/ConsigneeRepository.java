package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.ConsigneeVO;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ConsigneeRepository extends GenericReactiveRepository<ConsigneeVO, String> {

    <S extends ConsigneeVO> Flux<S> findAll(Example<S> example);
}
