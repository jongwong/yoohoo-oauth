package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.PurchaseGroupVO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PurchaseGroupRepository extends GenericReactiveRepository<PurchaseGroupVO, String> {

    @Query("SELECT p.*, d.name AS distribution_point_name " +
            "FROM tb_purchase_group p " +
            "JOIN tb_distribution_points d ON p.distribution_point_id = d.id " +
            "WHERE p.id = :id LIMIT 1")
    Mono<PurchaseGroupVO> customFindById(String id);
}
