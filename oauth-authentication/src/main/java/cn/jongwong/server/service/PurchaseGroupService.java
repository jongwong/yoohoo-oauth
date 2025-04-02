package cn.jongwong.server.service;

import cn.jongwong.server.common.AutoCreatedField;
import cn.jongwong.server.entity.PurchaseGroupProductVO;
import cn.jongwong.server.entity.PurchaseGroupVO;
import cn.jongwong.server.enums.GlobalEnableTypeEnum;
import cn.jongwong.server.repository.ProductRepository;
import cn.jongwong.server.repository.PurchaseGroupProductRepository;
import cn.jongwong.server.repository.PurchaseGroupRepository;
import cn.jongwong.server.service.product.PurchaseGroupProductService;
import cn.jongwong.server.util.response.EntityUtils;
import cn.jongwong.server.util.response.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class PurchaseGroupService {

    @Autowired
    private PurchaseGroupRepository purchaseGroupRepository;

    @Autowired
    private TransactionalOperator transactionalOperator;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    R2dbcEntityTemplate r2dbcEntityTemplate;

    @Autowired
    private PurchaseGroupProductRepository purchaseGroupProductRepository;

    @Autowired
    PurchaseGroupProductService purchaseGroupProductService;

    @Transient
    public Mono<PurchaseGroupVO> update(PurchaseGroupVO data) {


        EntityUtils.ensureIdExists(data);

        return userService.getCurrentUserReactive().map((u) -> {
                    data.setCreatedBy(u.getId());
                    data.setCreatedByName(u.getName());
                    data.setCreatedAt(LocalDateTime.now());
                    return data;
                }).flatMap((newData) -> purchaseGroupRepository.findById(newData.getId()).map(oldGroup -> {
                    newData.setCreatedAt(oldGroup.getCreatedAt());
                    newData.setCreatedBy(oldGroup.getCreatedBy());
                    newData.setStatus(oldGroup.getStatus());
                    newData.setEnable(oldGroup.getEnable());
                    newData.setCreatedByName(oldGroup.getCreatedByName());
                    return newData;
                })).flatMap((d) -> purchaseGroupRepository.save(d))
                .flatMap(updatedGroup -> {

                    // 设置purchaseGroupId
                    var products = data.getProducts();
                    products.forEach(product -> {
                        product.setPurchaseGroupId(data.getId());
                    });


                    return purchaseGroupProductRepository.saveRefAll(products)  // 保存商品信息
                            .collectList()
                            .then(Mono.just(updatedGroup)); // 返回更新后的团购信息
                });
    }

    @Transient
    public Mono<PurchaseGroupVO> create(@AutoCreatedField PurchaseGroupVO data) {
        data.setId(null);
        return userService.getCurrentUserReactive().map((u) -> {
            data.setCreatedBy(u.getId());
            data.setCreatedByName(u.getName());
            data.setCreatedAt(LocalDateTime.now());
            return data;
        }).flatMap((d) -> purchaseGroupRepository.insert(d)).flatMap(updatedGroup -> {

            // 设置purchaseGroupId
            var products = data.getProducts();
            products.forEach(product -> {
                product.setId(null);
                product.setPurchaseGroupId(data.getId());
            });


            return purchaseGroupProductRepository.saveRefAll(products)  // 保存商品信息
                    .collectList()
                    .then(Mono.just(updatedGroup)); // 返回更新后的团购信息
        });

    }


    public Mono<PurchaseGroupVO> findById(String id) {
        // 合并purchaseGroupRepository  purchaseGroupProductRepository


        return purchaseGroupRepository.findOneByIdDSL(id, sql -> sql.as("p")
                        .column("d.name as distribution_point_name")
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
                    var ids = e.getProducts().stream().map(PurchaseGroupProductVO::getProductId).toList();
                    // products 批量查商品找到商品名称和商code
                    return productService.findByIds(ids)
                            .collectList()
                            .map(list -> {
                                e.getProducts().forEach(product -> {
                                    list.stream().filter(p -> p.getId().equals(product.getProductId())).findFirst().ifPresent(p -> {
                                        product.setProductName(p.getName());
                                        product.setProductCode(p.getCode());
                                        product.setPrice(p.getPrice());
                                        product.setHasMultipleSku(p.getHasMultipleSku());
                                        product.setMarketPrice(p.getMarketPrice());
                                        product.setThumbnailImage(p.getThumbnailImage().getUrl());

                                    });
                                });
                                return e;
                            });
                });
    }

    public Mono<Void> deleteById(String id) {
        return purchaseGroupRepository.deleteById(id);
    }




    public Mono<PurchaseGroupVO> enable(String purchaseGroupId) {
        return findById(purchaseGroupId).map(e -> {
            e.setEnable(GlobalEnableTypeEnum.ENABLE.getValue());
            return e;
        }).flatMap((data) -> userService.getCurrentUserReactive().map((u) -> {
            data.setCreatedBy(u.getId());
            data.setCreatedByName(u.getName());
            data.setCreatedAt(LocalDateTime.now());
            return data;
        })).flatMap((e) -> purchaseGroupRepository.save(e));
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

    public Mono<Page<PurchaseGroupVO>> clientSearch(String name, Integer enable, Integer page, Integer size) {


        return purchaseGroupRepository.findPageByDSL(page, size, sql ->
                        sql.as("p")
                                .column("d.name as distribution_point_name")
                                .column("d.address as distribution_point_address")

                                .withJoin(t -> t.left()
                                        .table("tb_distribution_points d")
                                        .on("p.distribution_point_id = d.id"))
                                .eq("enable", enable).like("name", name)
                                .sort("created_at,desc"))

                .flatMap(pageData -> {
                    var ids = pageData.getData().stream().map(PurchaseGroupVO::getId).toList();

                    return purchaseGroupProductService.findAllByGroupIds(ids).collectList().map(products -> {
                        var dataList = pageData.getData();
                        dataList.forEach(group -> {
                            var filterProduct = products.stream().filter(p -> group.getId().equals(p.getPurchaseGroupId())).toList();
                            group.setProducts(filterProduct);
                        });

                        return pageData;
                    });


                });


    }


}
