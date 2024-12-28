package cn.jongwong.server.service;

import cn.jongwong.server.common.MapperUtil;
import cn.jongwong.server.common.QueryBuilder;
import cn.jongwong.server.entity.ProductImageVO;
import cn.jongwong.server.entity.ProductVO;
import cn.jongwong.server.enums.product.ProductArchivedStatus;
import cn.jongwong.server.repository.ProductImageRepository;
import cn.jongwong.server.repository.ProductRepository;
import cn.jongwong.server.util.response.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    R2dbcEntityTemplate r2dbcEntityTemplate;

    @Autowired
    ProductImageService productImageService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionalOperator transactionalOperator;

    // 根据商品ID查询商品信息和图片，并进行分组
    public Mono<ProductVO> getProductWithImagesById(String id) {
        // 1. 查询商品信息
        Mono<ProductVO> baseProduct = productRepository.findById(id);


        // 2. 查询商品图片
        Flux<ProductImageVO> imagesFlux = productImageRepository.findByProductId(id);

        // 3. 聚合商品信息和图片
        return baseProduct.zipWith(imagesFlux.collectList(), (productVO, images) -> {
            ProductVO newProductVO = new ProductVO();
            // 复制属性
            BeanUtils.copyProperties(productVO, newProductVO);

            // 4. 分组图片类型
            Map<Integer, List<ProductImageVO>> groupedImages = images.stream()
                    .collect(Collectors.groupingBy(ProductImageVO::getImageType));

            // 5. 设置商品的图片字段
            newProductVO.setMainImage(findImageByType(groupedImages, 1)); // 主图
            newProductVO.setThumbnailImage(findImageByType(groupedImages, 2)); // 缩略图
            newProductVO.setCarouselImages(findImageByType(groupedImages, 3)); // 轮播图
            newProductVO.setOtherImages(findImageByType(groupedImages, 4)); // 其他图片

            return newProductVO;
        });
    }

    public List<ProductImageVO> getImageGroup(ProductVO productVO) {
        // 合并所有图片
        List<ProductImageVO> allImages = new ArrayList<>();

        // 添加主图，若主图为 null 则添加空列表
        if (productVO.getMainImage() != null) {
            for (ProductImageVO image : productVO.getMainImage()) {
                image.setImageType(1); // 1 表示主图
                allImages.add(image);
            }
        }

        // 添加缩略图，若缩略图为 null 则添加空列表
        if (productVO.getThumbnailImage() != null) {
            for (ProductImageVO image : productVO.getThumbnailImage()) {
                image.setImageType(2); // 2 表示缩略图
                allImages.add(image);
            }
        }

        // 添加轮播图，若轮播图为 null 则添加空列表
        if (productVO.getCarouselImages() != null) {
            for (ProductImageVO image : productVO.getCarouselImages()) {
                image.setImageType(3); // 3 表示轮播图
                allImages.add(image);
            }
        }

        // 添加其他图片，若其他图片为 null 则添加空列表
        if (productVO.getOtherImages() != null) {
            for (ProductImageVO image : productVO.getOtherImages()) {
                image.setImageType(4); // 4 表示其他图片
                allImages.add(image);
            }
        }

        // 返回合并后的图片列表
        return allImages;
    }


    // 根据图片类型获取单张图片
    private List<ProductImageVO> findImageByType(Map<Integer, List<ProductImageVO>> groupedImages, int type) {
        List<ProductImageVO> images = groupedImages.getOrDefault(type, Collections.emptyList());
        if (images.isEmpty()) {
            return null;  // 如果没有对应类型的图片，返回 null
        }
        // 假设每个类型的图片有多张，返回第一张图片
        return images; // 或根据其他规则选择图片
    }

    @Transactional
    public Mono<ProductVO> updateProductWithImages(String id, ProductVO updatedProductVO) {

        return productRepository.findById(id).flatMap(existingProductVO -> {
                    if (existingProductVO.getCode() == null) {
                        return productRepository.findProductWithMaxCode()
                                .flatMap(find -> {
                                    Integer newCode = (find != null && find.getCode() != null) ? find.getCode() + 1 : 1; // 如果 find.getCode() 为 null，默认从 1 开始
                                    existingProductVO.setCode(newCode);
                                    return productRepository.save(existingProductVO); // 保存更新后的产品
                                });
                    }
                    return Mono.just(existingProductVO); // 如果 code 已经有值，则直接返回 existingProduct
                })
                .flatMap(existingProductVO -> {
                    // 更新商品信息
                    BeanUtils.copyProperties(updatedProductVO, existingProductVO);


                    // 获取与商品相关的所有图片
                    List<ProductImageVO> imgs = getImageGroup(updatedProductVO);


                    // 查找当前商品所有图片
                    return productImageRepository.findByProductId(id)
                            .collectList() // 收集所有当前商品的图片
                            .flatMap(existingImages -> {
                                // 找出需要删除的图片
                                List<ProductImageVO> imagesToDelete = existingImages.stream()
                                        .filter(existingImage -> imgs.stream()
                                                .noneMatch(newImage -> {
                                                    return areProductAndUrlEqual(newImage, existingImage);
                                                }))
                                        .collect(Collectors.toList());

                                // 删除这些图片，保存更新后的商品，并处理新增/更新图片
                                return productImageRepository.deleteAll(imagesToDelete)
                                        .then(productRepository.save(existingProductVO))
                                        .flatMap(savedProductVO -> Flux.fromIterable(imgs)
                                                .flatMap(image -> productImageService.save(image, id)) // 保存新增/更新的图片
                                                .then(Mono.just(savedProductVO)));
                            });
                })
                .as(transactionalOperator::transactional); // 使用事务管理
    }

    // 创建商品
    public Mono<ProductVO> createProduct(ProductVO productVO) {
        return productRepository.save(productVO); // 保存商品到数据库
    }


    // 删除商品
    public Mono<Void> deleteProduct(String id) {
        return productRepository.findById(id)
                .flatMap(existingProductVO -> productRepository.deleteById(id)); // 删除商品
    }


    public Mono<Page<ProductVO>> getProductList(String name, int page, int size) {


        return new QueryBuilder<>(r2dbcEntityTemplate, ProductVO.class)
                .addLikeCondition("name", name)
                .executeQuery(page, size).map(pageData -> {
                    // 转换 User -> UserRes
                    List<ProductVO> userResList = pageData.getData().stream()
                            .map(user -> MapperUtil.mapFields(user, ProductVO.class))
                            .toList();

                    // 构建新的 Page<UserRes>
                    return new Page<>(
                            userResList,
                            pageData.getTotal(),
                            pageData.getPage(),
                            pageData.getSize()
                    );
                });


    }


    // 审核通过
    public Mono<ProductVO> approveProduct(String productId) {
        return productRepository.findById(productId)
                .flatMap(productVO -> {
                    productVO.setArchivedStatus(ProductArchivedStatus.COMPLETED.getCode()); // 审核完成
                    productVO.setRejectionReason(null); // 清除拒绝原因
                    return productRepository.save(productVO);
                });
    }

    public boolean areProductAndUrlEqual(ProductImageVO newImage, ProductImageVO existingImage) {
        return Objects.equals(newImage.getProductId(), existingImage.getProductId()) &&
                Objects.equals(newImage.getUrl(), existingImage.getUrl());
    }

    // 审核拒绝
    public Mono<ProductVO> rejectProduct(String productId, String rejectionReason) {
        return productRepository.findById(productId)
                .flatMap(productVO -> {
                    productVO.setArchivedStatus(ProductArchivedStatus.REJECTED.getCode()); // 审核完成
                    productVO.setRejectionReason(rejectionReason); // 清除拒绝原因
                    return productRepository.save(productVO);
                });
    }

    // 保存草稿
    @Transactional
    public Mono<ProductVO> saveDraftProduct(String productId, ProductVO productVO) {
        productVO.setId(productId);
        productVO.setStatus(null);
        productVO.setListedStatus(null);
        productVO.setArchivedStatus(ProductArchivedStatus.DRAFT.getCode());
        return updateProductWithImages(productId, productVO);
    }

    @Transactional
    public Mono<ProductVO> submit(String productId, ProductVO productVO) {
        productVO.setId(productId);
        productVO.setArchivedStatus(ProductArchivedStatus.IN_REVIEW.getCode());
        return updateProductWithImages(productId, productVO);
    }


}
