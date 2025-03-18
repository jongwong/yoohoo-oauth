package cn.jongwong.server.service;

import cn.jongwong.server.common.MapperUtil;
import cn.jongwong.server.common.QueryBuilder;
import cn.jongwong.server.entity.ProductVO;
import cn.jongwong.server.enums.product.ProductArchivedStatus;
import cn.jongwong.server.repository.ProductImageRepository;
import cn.jongwong.server.repository.ProductRepository;
import cn.jongwong.server.repository.ProductSkuRepository;
import cn.jongwong.server.util.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private UserService userService;

    @Autowired
    R2dbcEntityTemplate r2dbcEntityTemplate;


    @Autowired
    private TransactionalOperator transactionalOperator;
    @Autowired
    private ProductSkuRepository productSkuRepository;


    // 提交审核
    @Transactional
    public Mono<ProductVO> submit(String productId, ProductVO productVO) {
        return productRepository.findById(productId).flatMap((old) -> {
                    if (isSubmitAble(old)) {
                        var newP = productVO.toBuilder()
                                .id(productId)
                                .archivedStatus(ProductArchivedStatus.REVIEWING.getCode())
                                .updatedAt(LocalDateTime.now())
                                .build();
                        return Mono.just(newP);
                    } else {
                        return Mono.error(new IllegalStateException("该状态不允许提交审核"));
                    }

                }).flatMap(this::update)
                .flatMap(p -> productSkuRepository.deleteAllByProductId(productId))
                .then(Mono.defer(() -> {
                    var skus = productVO.getSkus().stream().map(sku -> {
                        sku.setProductId(productId);
                        return sku;
                    }).toList();
                    return productSkuRepository.saveRefAll(skus).collectList();
                })).flatMap(p -> findById(productId));
    }

    private boolean isSubmitAble(ProductVO product) {
        return product.getArchivedStatus() == ProductArchivedStatus.DRAFT.getCode()
                || product.getArchivedStatus() == ProductArchivedStatus.REJECTED.getCode();
    }


    // 创建商品
    public Mono<ProductVO> create(ProductVO data) {
        // 检查商品ID是否为空，如果已有ID则返回错误
        if (data.getId() != null) {
            return Mono.error(new IllegalArgumentException("无法创建已存在的数据"));
        }

        // 获取最大商品代码并更新商品代码
        return productRepository.findProductWithMaxCode()
                .map(product -> {
                    int newCode = (product != null && product.getCode() != null) ? product.getCode() + 1 : 1;
                    data.setCode(newCode);
                    return data;
                })
                .flatMap(updatedProduct ->
                        // 获取当前用户ID，并构建新的商品数据
                        userService.getCurrentUserReactive()
                                .map(u -> updatedProduct.toBuilder()
                                        .createdAt(LocalDateTime.now())
                                        .updatedAt(LocalDateTime.now())
                                        .createdBy(u.getId())
                                        .createdByName(u.getName())
                                        .id(UUID.randomUUID().toString())
                                        .build())
                                .flatMap(productRepository::insert)
                );
    }


    private Mono<ProductVO> update(ProductVO productVO) {
        return userService.getCurrentUserReactive().map(u -> {
            productVO.setUpdatedBy(u.getId());
            productVO.setUpdatedByName(u.getName());
            productVO.setUpdatedAt(LocalDateTime.now());
            return productVO;
        }).flatMap(productRepository::save);
    }

    @Transactional
    public Mono<ProductVO> save(String productId, ProductVO productVO) {
        productVO.setId(productId);

        return productRepository.findById(productId).flatMap((old) -> {
                    if (old.getArchivedStatus() != ProductArchivedStatus.DRAFT.getCode()) {
                        return Mono.error(new IllegalStateException("只能保存草稿状态的商品"));
                    }
                    productVO.setArchivedStatus(ProductArchivedStatus.DRAFT.getCode());
                    productVO.setListedStatus(old.getListedStatus());

                    return Mono.just(productVO);

                }).flatMap(this::update)
                .flatMap(p -> productSkuRepository.deleteAllByProductId(productId))
                .then(Mono.defer(() -> {
                    var skus = productVO.getSkus().stream().map(sku -> {
                        sku.setProductId(productId);
                        return sku;
                    }).toList();
                    return productSkuRepository.saveRefAll(skus).collectList();
                })).flatMap(p -> findById(productId));

    }


    public Mono<PageResponse<ProductVO>> search(String name, String status, int page, int size) {


        return new QueryBuilder<>(r2dbcEntityTemplate, ProductVO.class)
                .addLikeCondition("name", name)
                .paginate(page, size)
                .exec().map(pageData -> {
                    List<ProductVO> userResList = pageData.getData().stream()
                            .map(user -> MapperUtil.mapFields(user, ProductVO.class))
                            .toList();

                    return new PageResponse<>(
                            userResList,
                            pageData.getTotal()
                    );
                });


    }



    public Mono<ProductVO> reject(String id, String rejectionReason) {
        return productRepository.findById(id)
                .map(couponsVO -> {
                    if (couponsVO.getArchivedStatus() == ProductArchivedStatus.REVIEWING.getCode()) { // 仅审核中的优惠券可以被拒绝
                        couponsVO.setArchivedStatus(ProductArchivedStatus.REJECTED.getCode()); // 设置状态为 "审核拒绝"
                        couponsVO.setRejectionReason(rejectionReason);
                        couponsVO.setUpdatedAt(LocalDateTime.now());
                        return couponsVO;
                    } else {
                        throw new IllegalStateException("仅审核中的商品可以被拒绝");
                    }
                })
                .flatMap(productRepository::save)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("商品未找到")));
    }


    public Mono<ProductVO> approve(String id) {
        return productRepository.findById(id)
                .map(couponsVO -> {
                    if (couponsVO.getArchivedStatus() == ProductArchivedStatus.REVIEWING.getCode()) { // 仅审核中的优惠券可以被审核通过
                        couponsVO.setArchivedStatus(ProductArchivedStatus.COMPLETED.getCode()); // 设置状态为 "审核通过"
                        couponsVO.setUpdatedAt(LocalDateTime.now());
                        return couponsVO;
                    } else {
                        throw new IllegalStateException("仅审核中的商品可以被审核通过");
                    }
                })
                .flatMap(this::update)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("商品未找到")));
    }

    public Mono<String> delete(String id) {
        return productRepository.findById(id).switchIfEmpty(Mono.error(new IllegalArgumentException("商品未找到")))
                .map(data -> {
                    if (data.getArchivedStatus() == ProductArchivedStatus.DRAFT.getCode() || data.getArchivedStatus() == ProductArchivedStatus.REJECTED.getCode()) { // 仅审核中的优惠券可以被审核通过
                        return id;
                    } else {
                        throw new IllegalStateException("仅草稿中或者审核拒绝的商品可以被删除");
                    }
                })
                .flatMap(productRepository::deleteById).then(Mono.fromCallable(() -> id));
    }

    public Flux<ProductVO> findByIds(List<String> ids) {
        if (ids == null) {
            return Flux.empty();
        }
        return productRepository.findAllByIdIn(ids);
    }

    public Mono<ProductVO> findById(String id) {

        return productRepository.findById(id);
    }

    public Mono<ProductVO> findByIdWithSku(String id) {

        return productRepository.findById(id).flatMap(productVO -> {
            var skus = productSkuRepository.findAllByProductId(id).collectList();
            return Mono.zip(Mono.just(productVO), skus).map(tuple -> {
                var product = tuple.getT1();
                product.setSkus(tuple.getT2());
                return product;
            });
        });
    }


}
