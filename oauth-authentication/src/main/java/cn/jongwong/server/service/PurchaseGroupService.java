package cn.jongwong.server.service;

import cn.jongwong.server.common.AutoCreatedField;
import cn.jongwong.server.entity.PurchaseGroupProductVO;
import cn.jongwong.server.entity.PurchaseGroupVO;
import cn.jongwong.server.repository.ProductRepository;
import cn.jongwong.server.repository.PurchaseGroupProductRepository;
import cn.jongwong.server.repository.PurchaseGroupRepository;
import cn.jongwong.server.util.response.EntityUtils;
import cn.jongwong.server.util.response.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class PurchaseGroupService {

    @Autowired
    private PurchaseGroupRepository purchaseGroupRepository;

    @Autowired
    private TransactionalOperator transactionalOperator;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    R2dbcEntityTemplate r2dbcEntityTemplate;

    @Autowired
    private PurchaseGroupProductRepository purchaseGroupProductRepository;


    public Mono<PurchaseGroupVO> update(PurchaseGroupVO data) {


        EntityUtils.ensureIdExists(data);

        // 显式事务控制
        return transactionalOperator.transactional(
                purchaseGroupRepository.save(data)  // 保存团购信息
                        .flatMap(updatedGroup -> {

                            // 设置purchaseGroupId
                            var products = data.getProducts();
                            products.forEach(product -> {
                                product.setPurchaseGroupId(data.getId());
                            });


                            return purchaseGroupProductRepository.saveRefAll(products)  // 保存商品信息
                                    .collectList()
                                    .then(Mono.just(updatedGroup)); // 返回更新后的团购信息
                        })
        );
    }

    public Mono<PurchaseGroupVO> create(@AutoCreatedField PurchaseGroupVO data) {

        // 显式事务控制
        return transactionalOperator.transactional(
                purchaseGroupRepository.insert(data)  // 保存团购信息
                        .flatMap(updatedGroup -> {

                            // 设置purchaseGroupId
                            var products = data.getProducts();
                            products.forEach(product -> {
                                product.setPurchaseGroupId(data.getId());
                            });


                            return purchaseGroupProductRepository.saveRefAll(products)  // 保存商品信息
                                    .collectList()
                                    .then(Mono.just(updatedGroup)); // 返回更新后的团购信息
                        })
        );
    }
    public Mono<PurchaseGroupVO> findById(String id) {
        // 合并purchaseGroupRepository  purchaseGroupProductRepository


        return purchaseGroupRepository.findOneByDSL(id, sql -> sql.as("p").column("d.name as distribution_point_name")
                        .column("d.address as distribution_point_address")

                        .withJoin(t -> t.left()
                                .table("tb_distribution_points d")
                                .on("p.distribution_point_id = d.id")))
                .flatMap(group -> {
                    return purchaseGroupProductRepository.findAllByPurchaseGroupId(id)
                            .collectList()
                            .map(list -> {
                                group.setProducts(list);
                                return group;
                            });
                })

                .flatMap((e) -> {
                    // products 批量查商品找到商品名称和商code
                    return productRepository.findAllById(e.getProducts().stream().map(PurchaseGroupProductVO::getProductId).collect(Collectors.toList()))
                            .collectList()
                            .map(list -> {
                                e.getProducts().forEach(product -> {
                                    list.stream().filter(p -> p.getId().equals(product.getProductId())).findFirst().ifPresent(p -> {
                                        product.setProductName(p.getName());
                                        product.setProductCode(p.getCode());
                                    });
                                });
                                return e;
                            });
                });
    }

    public Mono<Void> deleteById(String id) {
        return purchaseGroupRepository.deleteById(id);
    }


    /**
     * 查询团购关联的商品以及最大库存
     *
     * @param purchaseGroupId 团购ID
     * @return Mono<PurchaseGroupProductVO> 商品及最大库存信息
     */
    public Mono<PurchaseGroupProductVO> findProductsByPurchaseGroupId(String purchaseGroupId) {
        return purchaseGroupProductRepository.findByPurchaseGroupId(purchaseGroupId);
    }

    public Mono<Page<PurchaseGroupVO>> search(String name, Integer enable, Integer page, Integer size) {

        return purchaseGroupRepository.findPageByDSL(page, size, sql ->
                sql.as("p")
                        .column("d.name as distribution_point_name")
                        .column("d.address as distribution_point_address")
                        .eq("enable", enable).like("name", name)
                        .withJoin(t -> t.left()
                                .table("tb_distribution_points d")
                                .on("p.distribution_point_id = d.id")));


    }

}
