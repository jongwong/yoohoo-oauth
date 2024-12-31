package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.ThirdPartyLoginVO;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdPartyLoginRepository extends GenericReactiveRepository<ThirdPartyLoginVO, String> {


}
