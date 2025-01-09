package cn.jongwong.server.service.product;

import cn.jongwong.server.common.AutoCreatedField;
import cn.jongwong.server.common.AutoUpdatedAspect;
import cn.jongwong.server.common.AutoUpdatedField;
import cn.jongwong.server.entity.PurchaseGroupVO;
import cn.jongwong.server.repository.PurchaseGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class PurchaseGroupService {

    @Autowired
    private PurchaseGroupRepository purchaseGroupRepository;
    @Autowired
    private AutoUpdatedAspect autoUpdatedAspect;

    public Mono<PurchaseGroupVO> update(@AutoUpdatedField PurchaseGroupVO purchaseGroupVO) {
        return purchaseGroupRepository.save(purchaseGroupVO);
    }

    public Mono<PurchaseGroupVO> create(@AutoCreatedField PurchaseGroupVO purchaseGroupVO) {
        purchaseGroupVO.setId(UUID.randomUUID().toString());
        return purchaseGroupRepository.insert(purchaseGroupVO);
    }

    public Mono<PurchaseGroupVO> findById(String id) {
        return purchaseGroupRepository.findById(id);
    }

    public Mono<Void> deleteById(String id) {
        return purchaseGroupRepository.deleteById(id);
    }
}
