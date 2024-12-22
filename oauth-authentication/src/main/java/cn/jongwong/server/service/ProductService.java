package cn.jongwong.server.service;

import cn.jongwong.server.common.MapperUtil;
import cn.jongwong.server.common.QueryBuilder;
import cn.jongwong.server.entity.Product;
import cn.jongwong.server.entity.ProductImage;
import cn.jongwong.server.repository.ProductImageRepository;
import cn.jongwong.server.repository.ProductRepository;
import cn.jongwong.server.util.response.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    R2dbcEntityTemplate r2dbcEntityTemplate;


    // 创建商品
    public Mono<Product> createProduct(Product product) {
        return productRepository.save(product); // 保存商品到数据库
    }

    // 根据商品ID查询商品信息和图片，并进行分组
    public Mono<Product> getProductWithImagesById(String id) {
        // 1. 查询商品信息
        Mono<Product> productMono = productRepository.findById(id);

        // 2. 查询商品图片
        Flux<ProductImage> imagesFlux = productImageRepository.findByProductId(id);

        // 3. 聚合商品信息和图片
        return productMono.zipWith(imagesFlux.collectList(), (product, images) -> {
            // 4. 分组图片类型
            Map<Integer, List<ProductImage>> groupedImages = images.stream()
                    .collect(Collectors.groupingBy(ProductImage::getImageType));

            // 5. 设置商品的图片字段
            product.setMainImage(findImageByType(groupedImages, 1)); // 主图
            product.setThumbnailImage(findImageByType(groupedImages, 2)); // 缩略图
            product.setCarouselImages(findImageByType(groupedImages, 3)); // 轮播图
            product.setOtherImages(findImageByType(groupedImages, 4)); // 其他图片

            return product;
        });
    }


    // 根据图片类型获取单张图片
    private List<ProductImage> findImageByType(Map<Integer, List<ProductImage>> groupedImages, int type) {
        List<ProductImage> images = groupedImages.getOrDefault(type, Collections.emptyList());
        if (images.isEmpty()) {
            return null;  // 如果没有对应类型的图片，返回 null
        }
        // 假设每个类型的图片有多张，返回第一张图片
        return images; // 或根据其他规则选择图片
    }

    // 查询所有商品
    public Flux<Product> getAllProducts() {
        return productRepository.findAll(); // 查询所有商品
    }

    // 更新商品
    public Mono<Product> updateProduct(String id, Product updatedProduct) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    // 更新商品字段
                    existingProduct.setName(updatedProduct.getName());
                    existingProduct.setDescription(updatedProduct.getDescription());
                    existingProduct.setShortDescription(updatedProduct.getShortDescription());
                    existingProduct.setPrice(updatedProduct.getPrice());
                    existingProduct.setCostPrice(updatedProduct.getCostPrice());
                    existingProduct.setSku(updatedProduct.getSku());
                    existingProduct.setStatus(updatedProduct.getStatus());
                    existingProduct.setMetaTitle(updatedProduct.getMetaTitle());
                    existingProduct.setMetaDescription(updatedProduct.getMetaDescription());
                    existingProduct.setMetaKeywords(updatedProduct.getMetaKeywords());
                    existingProduct.setArchivedStatus(updatedProduct.getArchivedStatus());
                    existingProduct.setListedStatus(updatedProduct.getListedStatus());
                    return productRepository.save(existingProduct); // 更新并保存商品
                });
    }

    // 删除商品
    public Mono<Void> deleteProduct(String id) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> productRepository.deleteById(id)); // 删除商品
    }


    public Mono<Page<Product>> getProductList(String name, int page, int size) {


        return new QueryBuilder<>(r2dbcEntityTemplate, Product.class)
                .addLikeCondition("name", name)
                .executeQuery(page, size).map(pageData -> {
                    // 转换 User -> UserRes
                    List<Product> userResList = pageData.getData().stream()
                            .map(user -> MapperUtil.mapFields(user, Product.class))
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
}
