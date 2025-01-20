package cn.jongwong.server.service.product;

import cn.jongwong.server.entity.ClientPurchaseGroupProductVO;
import cn.jongwong.server.enums.product.ProductImageType;
import cn.jongwong.server.repository.ClientPurchaseGroupProductRepository;
import cn.jongwong.server.util.response.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class PurchaseGroupProductService {
    @Autowired
    private ClientPurchaseGroupProductRepository clientPurchaseGroupProductRepository;

    public Mono<Page<ClientPurchaseGroupProductVO>> search(String productName, String categoryId, String distributionPointId, Integer[] groupStatus, Integer listedStatus, Integer enable,
                                                           LocalDateTime timeDeliveryStart, LocalDateTime timeDeliveryEnd, LocalDateTime timeGroupStart, LocalDateTime timeGroupEnd, Integer page, Integer size) {

        return clientPurchaseGroupProductRepository.findPageByDSL(page, size, sql ->
                sql.as("pgp")
                        .column("pgp.id AS group_product_id")
                        .column("pgp.purchase_group_id")
                        .column("pgp.product_id")
                        .column("pgp.max_stock")
                        .column("pgp.sold_quantity")
                        .column("p.code")
                        .column("p.name")
                        .column("p.price")


                        .column("p.listed_status")
                        .column("p.archived_status")
                        .column("p.category_name")
                        .column("p.category_id")
                        .column("p.category_code")
                        .field("pg.name", "group_name")
                        .field("pg.status", "group_status")
                        .field("pg.enable", "group_enable")
                        .column("pg.time_start AS time_group_start")
                        .column("pg.time_end AS time_group_end")
                        .column("pg.time_delivery_start")
                        .column("pg.time_delivery_end")
                        .column("pg.distribution_point_id")

                        .eq("pg.distribution_point_id", distributionPointId)
                        .eq("pg.enable", enable)
                        .like("p.name", productName)
                        .eq("p.category_id", categoryId)
                        .eq("p.status", listedStatus)
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

    public Mono<Page<ClientPurchaseGroupProductVO>> searchWithImage(String productName, String categoryId, String distributionPointId, Integer[] groupStatus, Integer listedStatus, Integer enable,
                                                                    LocalDateTime timeDeliveryStart, LocalDateTime timeDeliveryEnd, LocalDateTime timeGroupStart, LocalDateTime timeGroupEnd, Integer page, Integer size) {

        return clientPurchaseGroupProductRepository.findPageByDSL(page, size, sql ->
                        sql.as("pgp")
                                .column("pgp.id AS group_product_id")
                                .column("pgp.purchase_group_id")
                                .column("pgp.product_id")
                                .column("pgp.max_stock")
                                .column("pgp.sold_quantity")
                                .column("p.code")
                                .column("p.name")
                                .column("p.price")

//                        .column("p.thumbnail_image")
                                .column("p.listed_status")
                                .column("p.archived_status")
                                .column("p.category_name")
                                .column("p.category_id")
                                .column("p.category_code")
                                .field("pg.name", "group_name")
                                .field("pg.status", "group_status")
                                .field("pg.enable", "group_enable")
                                .column("pg.time_start AS time_group_start")
                                .column("pg.time_end AS time_group_end")
                                .column("pg.time_delivery_start")
                                .column("pg.time_delivery_end")
                                .column("pg.distribution_point_id")
                                .column("p_img.url AS thumbnail_image_url")


                                .eq("pg.distribution_point_id", distributionPointId)
                                .eq("pg.enable", enable)
                                .like("p.name", productName)
                                .eq("p.category_id", categoryId)
                                .eq("p.status", listedStatus)
                                .eq("pg.status", groupStatus)
                                .customCondition("pg.time_delivery_start", ">=", timeDeliveryStart)
                                .customCondition("pg.time_delivery_end", "<=", timeDeliveryEnd)
                                .customCondition("pg.time_start", ">=", timeGroupStart)
                                .customCondition("pg.time_end", "<=", timeGroupEnd)

                                .withJoin(t -> t.left()
                                        .table("tb_product p")
                                        .on("pgp.product_id = p.id"))
                                .withJoin(t -> t.left()
                                        .table("tb_product_images p_img")
                                        .on("pgp.product_id = p_img.product_id  AND p_img.image_type =  " + ProductImageType.THUMBNAIL.getCode()))
                                .withJoin(t -> t.left()
                                        .table("tb_purchase_group pg")
                                        .on("pgp.purchase_group_id = pg.id"))

        );

    }

}
