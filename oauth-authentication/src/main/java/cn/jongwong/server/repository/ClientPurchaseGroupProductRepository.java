package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.ClientPurchaseGroupProductVO;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientPurchaseGroupProductRepository extends GenericReactiveRepository<ClientPurchaseGroupProductVO, String> {

}
