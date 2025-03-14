package cn.jongwong.server.service;

import cn.jongwong.server.common.MapperUtil;
import cn.jongwong.server.common.QueryBuilder;
import cn.jongwong.server.entity.ProductImageVO;
import cn.jongwong.server.entity.ProductVO;
import cn.jongwong.server.enums.coupons.CouponsStatus;
import cn.jongwong.server.enums.product.ProductArchivedStatus;
import cn.jongwong.server.enums.product.ProductImageType;
import cn.jongwong.server.repository.ProductImageRepository;
import cn.jongwong.server.repository.ProductRepository;
import cn.jongwong.server.util.response.Page;
import cn.jongwong.server.util.response.PageResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    // 获取商品信息与相关图片
    public Mono<ProductVO> getProductWithImagesById(String id) {
        Mono<ProductVO> baseProduct = productRepository.findById(id);
        Flux<ProductImageVO> imagesFlux = productImageRepository.findByProductId(id);

        return baseProduct.zipWith(imagesFlux.collectList(), (productVO, images) -> {
            ProductVO newProductVO = productVO.toBuilder().build();
            Map<Integer, List<ProductImageVO>> groupedImages = images.stream()
                    .collect(Collectors.groupingBy(ProductImageVO::getImageType));

            newProductVO.setMainImage(findImageByType(groupedImages, 1));
            newProductVO.setThumbnailImage(findImageByType(groupedImages, 2));
            newProductVO.setCarouselImages(findImageByType(groupedImages, 3));
            newProductVO.setOtherImages(findImageByType(groupedImages, 4));

            return newProductVO;
        });
    }

    private List<ProductImageVO> findImageByType(Map<Integer, List<ProductImageVO>> groupedImages, int type) {
        return Optional.ofNullable(groupedImages.get(type)).orElse(Collections.emptyList());
    }

    // 合并商品图片
    public List<ProductImageVO> getImageGroup(ProductVO productVO) {
        List<ProductImageVO> allImages = new ArrayList<>();
        addImageGroup(allImages, productVO.getMainImage(), 1);
        addImageGroup(allImages, productVO.getThumbnailImage(), 2);
        addImageGroup(allImages, productVO.getCarouselImages(), 3);
        addImageGroup(allImages, productVO.getOtherImages(), 4);
        return allImages;
    }

    private void addImageGroup(List<ProductImageVO> allImages, List<ProductImageVO> imageList, int type) {
        if (imageList != null) {
            imageList.forEach(image -> {
                image.setImageType(type);
                allImages.add(image);
            });
        }
    }

    @Transactional
    public Mono<ProductVO> updateProductWithImages(String id, ProductVO updatedProductVO) {
        return productRepository.findById(id)
                .flatMap(this::updateProductCodeIfNeeded)
                .flatMap(existingProduct -> updateProductInfoAndImages(id, updatedProductVO, existingProduct))
                .as(transactionalOperator::transactional);
    }

    private Mono<ProductVO> updateProductCodeIfNeeded(ProductVO existingProduct) {
        if (existingProduct.getCode() == null) {
            return productRepository.findProductWithMaxCode()
                    .flatMap(product -> {
                        int newCode = (product != null && product.getCode() != null) ? product.getCode() + 1 : 1;
                        existingProduct.setCode(newCode);
                        return productRepository.save(existingProduct);
                    });
        }
        return Mono.just(existingProduct);
    }

    private Mono<ProductVO> updateProductInfoAndImages(String id, ProductVO updatedProductVO, ProductVO existingProductVO) {
        BeanUtils.copyProperties(updatedProductVO, existingProductVO);
        List<ProductImageVO> imagesToSave = getImageGroup(updatedProductVO);

        return productImageRepository.findByProductId(id)
                .collectList()
                .flatMap(existingImages -> deleteAndSaveImages(existingImages, imagesToSave, id, existingProductVO));
    }

    private Mono<ProductVO> deleteAndSaveImages(List<ProductImageVO> existingImages, List<ProductImageVO> newImages,
                                                String productId, ProductVO updatedProductVO) {
        List<ProductImageVO> imagesToDelete = findImagesToDelete(existingImages, newImages);
        return productImageRepository.deleteAll(imagesToDelete)
                .then(productRepository.save(updatedProductVO))
                .flatMap(savedProduct -> saveNewImages(newImages, productId, savedProduct));
    }

    private List<ProductImageVO> findImagesToDelete(List<ProductImageVO> existingImages, List<ProductImageVO> newImages) {
        return existingImages.stream()
                .filter(existingImage -> newImages.stream().noneMatch(newImage -> areProductAndUrlEqual(newImage, existingImage)))
                .collect(Collectors.toList());
    }

    private Mono<ProductVO> saveNewImages(List<ProductImageVO> newImages, String productId, ProductVO savedProduct) {
        return Flux.fromIterable(newImages)
                .flatMap(image -> productImageService.save(image, productId))
                .then(Mono.just(savedProduct));
    }

    public boolean areProductAndUrlEqual(ProductImageVO newImage, ProductImageVO existingImage) {
        return Objects.equals(newImage.getProductId(), existingImage.getProductId()) &&
                Objects.equals(newImage.getUrl(), existingImage.getUrl());
    }

    // 提交审核
    @Transactional
    public Mono<ProductVO> submit(String id, ProductVO data) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    if (isSubmitAble(existingProduct)) {
                        var updatedProduct = data.toBuilder()
                                .id(id)
                                .archivedStatus(ProductArchivedStatus.REVIEWING.getCode())
                                .updatedAt(LocalDateTime.now())
                                .build();
                        return productRepository.save(updatedProduct)
                                .flatMap(savedProduct -> updateProductWithImages(id, savedProduct).thenReturn(savedProduct));
                    } else {
                        return Mono.error(new IllegalStateException("优惠券状态不允许提交审核"));
                    }
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("优惠券未找到")));
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
                        userService.getCurrentUserReactiveId()
                                .map(userId -> updatedProduct.toBuilder()
                                        .createdAt(LocalDateTime.now())
                                        .updatedAt(LocalDateTime.now())
                                        .status(CouponsStatus.DRAFT.getCode())
                                        .createdBy(userId)
                                        .updatedBy(userId)
                                        .id(UUID.randomUUID().toString())
                                        .build())
                                .flatMap(productRepository::insert)
                );
    }


    @Transactional
    public Mono<ProductVO> update(String productId, ProductVO productVO) {
        productVO.setId(productId);
        if (productVO.getArchivedStatus() != ProductArchivedStatus.DRAFT.getCode()) {
            return Mono.error(new IllegalStateException("只能保存草稿状态的商品"));
        }
        return updateProductWithImages(productId, productVO);
    }

    public Mono<Page<ProductVO>> search(String name, String status, int page, int size) {


        return new QueryBuilder<>(r2dbcEntityTemplate, ProductVO.class)
                .addLikeCondition("name", name)
                .addEqualCondition("status", status)
                .paginate(page, size)
                .exec().map(pageData -> {
                    List<ProductVO> userResList = pageData.getData().stream()
                            .map(user -> MapperUtil.mapFields(user, ProductVO.class))
                            .toList();

                    return new Page<>(
                            userResList,
                            pageData.getTotal(),
                            pageData.getPage(),
                            pageData.getSize()
                    );
                });


    }

    public Mono<Page<ProductVO>> searchWithImage(String name, String status, int page, int size) {


        return productRepository.findPageByDSL(page, size, (sql) -> {

            var groupString = """
                    COALESCE(
                            JSON_ARRAYAGG(
                                    IF(p_img.id IS NOT NULL,
                                       JSON_OBJECT(
                                               'id', p_img.id,
                                               'url', p_img.url,
                                               'name', p_img.name
                                       ),
                                       NULL
                                    )
                            ),
                            JSON_ARRAY()
                    ) AS thumbnail_image
                    """;

            return sql.as("p").field(groupString, true).like("name", name)
                    .eq("status", status)
                    .withJoin(t -> t.left()

                            .table("tb_product_images p_img")
                            .on("p.id = p_img.product_id  AND p_img.image_type =  " + ProductImageType.THUMBNAIL.getCode()));

        }).map(pageData -> {
            List<ProductVO> userResList = pageData.getData().stream()
                    .map(user -> MapperUtil.mapFields(user, ProductVO.class))
                    .toList();

            return new Page<>(
                    userResList,
                    pageData.getTotal(),
                    pageData.getPage(),
                    pageData.getSize()
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
                .flatMap(productRepository::save)
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

    public Flux<ProductVO> findByIdsWithImage(List<String> ids) {
        if (ids == null) {
            return Flux.empty();
        }
        return productRepository.findAllByIdIn(ids).flatMap(e -> productImageRepository.findByProductId(e.getId())
                .collectList()
                .map(imgList -> {
                    // 按 imageType 归类
                    Map<Integer, List<ProductImageVO>> groupedImages = new HashMap<>();
                    for (ProductImageVO img : imgList) {
                        groupedImages.computeIfAbsent(img.getImageType(), k -> new ArrayList<>()).add(img);
                    }

                    e.setMainImage(findImageByType(groupedImages, 1));
                    e.setThumbnailImage(findImageByType(groupedImages, 2));
                    e.setCarouselImages(findImageByType(groupedImages, 3));
                    e.setOtherImages(findImageByType(groupedImages, 4));

                    return e;
                }));
    }


    public Mono<PageResponse<ProductVO>> searchProductWithImage(String name, String status, int page, int size) {
        return search(name, status, page, size)
                .flatMap(e -> {
                    List<String> ids = e.getData().stream().map(ProductVO::getId).toList();

                    return productImageService.findAllByProductIdIn(ids)
                            .collectList()
                            .map(imgList -> {
                                // 按 productId 分组
                                Map<String, List<ProductImageVO>> imageMap = new HashMap<>();
                                for (ProductImageVO img : imgList) {
                                    imageMap.computeIfAbsent(img.getProductId(), k -> new ArrayList<>()).add(img);
                                }


                                // 组装商品和图片
                                List<ProductVO> updatedProducts = e.getData().stream().map(product -> {
                                    ProductVO newProduct = product.toBuilder().build();
                                    List<ProductImageVO> productImages = imageMap.getOrDefault(product.getId(), new ArrayList<>());
                                    // 按 imageType 归类
                                    Map<Integer, List<ProductImageVO>> groupedImages = new HashMap<>();
                                    for (ProductImageVO img : productImages) {
                                        groupedImages.computeIfAbsent(img.getImageType(), k -> new ArrayList<>()).add(img);
                                    }

                                    newProduct.setMainImage(findImageByType(groupedImages, 1));
                                    newProduct.setThumbnailImage(findImageByType(groupedImages, 2));
                                    newProduct.setCarouselImages(findImageByType(groupedImages, 3));
                                    newProduct.setOtherImages(findImageByType(groupedImages, 4));

                                    return newProduct;
                                }).toList();

                                // 返回新的 PageResponse
                                return new PageResponse<ProductVO>(0, "", updatedProducts, e.getTotal());
                            });
                });
    }


}
