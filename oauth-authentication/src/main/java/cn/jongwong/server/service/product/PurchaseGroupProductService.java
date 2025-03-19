package cn.jongwong.server.service.product;

import cn.jongwong.server.entity.ClientPurchaseGroupProductVO;
import cn.jongwong.server.repository.ClientPurchaseGroupProductRepository;
import cn.jongwong.server.util.response.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseGroupProductService {
    @Autowired
    private ClientPurchaseGroupProductRepository clientPurchaseGroupProductRepository;

    private static final String[] COLUMNS = new String[]{
            "pgp.id AS group_product_id",
            "pgp.purchase_group_id",
            "pgp.product_id",
            "pgp.max_stock",
            "pgp.amount_offset",

            "(p.price - pgp.amount_offset) AS final_price ",
            "p.id AS product_id",
            "p.code AS product_code",
            "p.name as product_name",
            "p.main_image",
            "p.thumbnail_image",
            "p.has_multiple_sku",
            "p.price",
            "p.listed_status",
            "p.archived_status",
            "p.category_name",
            "p.category_id",
            "p.category_code",
            "pg.id AS purchase_group_id",
            "pg.name AS group_name",
            "pg.group_required_count AS group_required_count",
            "pg.status AS group_status",
            "pg.enable AS group_enable",
            "pg.time_start AS time_group_start",
            "pg.time_end AS time_group_end",
            "pg.time_delivery_start",
            "pg.time_delivery_end",
            "pg.distribution_point_id"
    };

    public Mono<Page<ClientPurchaseGroupProductVO>> search(String productName, String categoryId, String distributionPointId, Integer[] groupStatus, Integer listedStatus, Integer enable,
                                                           LocalDateTime timeDeliveryStart, LocalDateTime timeDeliveryEnd, LocalDateTime timeGroupStart, LocalDateTime timeGroupEnd, Integer page, Integer size) {

        return clientPurchaseGroupProductRepository.findPageByDSL(page, size, sql ->
                sql.as("pgp")
                        .columns(COLUMNS)

                        .eq("pg.distribution_point_id", distributionPointId)
                        .eq("pg.enable", enable)
                        .like("p.name", productName)
                        .eq("p.category_id", categoryId)
                        .eq("pg.status", groupStatus)
                        .customCondition("pg.time_delivery_start", ">=", timeDeliveryStart)
                        .customCondition("pg.time_delivery_end", "<=", timeDeliveryEnd)
                        .customCondition("pg.time_start", ">=", timeGroupStart)
                        .customCondition("pg.time_end", "<=", timeGroupEnd)

                        .withJoin(t -> t.left()
                                .table("tb_product p")
                                .on("pgp.product_id = p.id"))
                        .withJoin(t -> t.left()
                                .table("tb_purchase_group pg")
                                .on("pgp.purchase_group_id = pg.id"))

        );

    }


    public Mono<ClientPurchaseGroupProductVO> fineOneWithImage(String id) {
        return clientPurchaseGroupProductRepository.findOneByIdDSL(id, sql ->
                sql.as("pgp")
                        .columns(COLUMNS)
                        .withJoin(t -> t.left()
                                .table("tb_product p")
                                .on("pgp.product_id = p.id"))
                        .withJoin(t -> t.left()
                                .table("tb_purchase_group pg")
                                .on("pgp.purchase_group_id = pg.id")));
    }

    public Mono<ClientPurchaseGroupProductVO> fineOneBypProductGroupId(String groupId, String productId) {
        return clientPurchaseGroupProductRepository.findOneByDSL(sql ->
                sql.as("pgp")
                        .columns(COLUMNS)
                        .eq("pgp.purchase_group_id", groupId)
                        .eq("pgp.product_id", productId)
                        .withJoin(t -> t.left()
                                .table("tb_product p")
                                .on("pgp.product_id = p.id"))
                        .withJoin(t -> t.left()
                                .table("tb_purchase_group pg")
                                .on("pgp.purchase_group_id = pg.id")));
    }




    public Flux<ClientPurchaseGroupProductVO> findAllByIds(List<String> ids) {

        return clientPurchaseGroupProductRepository.findAllByDSL(sql ->
                sql.as("pgp")
                        .columns(COLUMNS)
                        .eq("pgp.id", ids)
                        .withJoin(t -> t.left()
                                .table("tb_product p")
                                .on("pgp.product_id = p.id"))
                        .withJoin(t -> t.left()
                                .table("tb_purchase_group pg")
                                .on("pgp.purchase_group_id = pg.id"))

        );

    }



}
