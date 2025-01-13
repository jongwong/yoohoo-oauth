package cn.jongwong.server.repository;

import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.ClientPurchaseGroupProductVO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ClientPurchaseGroupProductRepository extends GenericReactiveRepository<ClientPurchaseGroupProductVO, String> {

    @Query("""
            SELECT pgp.id AS group_product_id,
                    pgp.purchase_group_id, pgp.product_id, pgp.max_stock, pgp.sold_quantity,
                    p.code,
                   p.code,
                   p.name, 
                   p.price, 
                   p.listed_status,
                   p.archived_status,
                   p.category_name, p.category_id, p.category_code,
                   pg.name AS group_name, pg.status AS group_status, pg.enable AS group_enable,
                   pg.time_start AS time_group_start, pg.time_end AS time_group_end,
                   pg.time_delivery_start, pg.time_delivery_end,
                   pg.distribution_point_id
            FROM tb_purchase_group_product pgp
            JOIN tb_product p ON pgp.product_id = p.id
            JOIN tb_purchase_group pg ON pgp.purchase_group_id = pg.id
            WHERE (:productName IS NULL OR p.name LIKE CONCAT('%', :productName, '%'))
              AND (:categoryId IS NULL OR p.category_id = :categoryId)
              AND (:listedStatus IS NULL OR p.status = :listedStatus)
              AND (:enable IS NULL OR pg.enable = :enable)
              AND (:groupStatus IS NULL OR pg.status = :groupStatus)
              AND (:deliveryStartTime IS NULL OR pg.time_delivery_start >= :deliveryStartTime)
              AND (:deliveryEndTime IS NULL OR pg.time_delivery_end <= :deliveryEndTime)
            LIMIT :size OFFSET :offset
            """)
    Flux<ClientPurchaseGroupProductVO> findByDynamicConditions(
            String productName, String categoryId, Integer groupStatus, Integer listedStatus, Integer enable,
            String deliveryStartTime, String deliveryEndTime, int size, int offset);

    @Query("""
            SELECT COUNT(*) 
            FROM tb_purchase_group_product pgp
            JOIN tb_product p ON pgp.product_id = p.id
            JOIN tb_purchase_group pg ON pgp.purchase_group_id = pg.id
            WHERE (:productName IS NULL OR p.name LIKE CONCAT('%', :productName, '%'))
              AND (:categoryId IS NULL OR p.category_id = :categoryId)
              AND (:listedStatus IS NULL OR p.status = :listedStatus)
              AND (:enable IS NULL OR pg.enable = :enable)
              AND (:groupStatus IS NULL OR pg.status = :groupStatus)
              AND (:deliveryStartTime IS NULL OR pg.time_delivery_start >= :deliveryStartTime)
              AND (:deliveryEndTime IS NULL OR pg.time_delivery_end <= :deliveryEndTime)
            """)
    Mono<Long> countByDynamicConditions(
            String productName, String categoryId, Integer groupStatus, Integer listedStatus, Integer enable,
            String deliveryStartTime, String deliveryEndTime);
}
