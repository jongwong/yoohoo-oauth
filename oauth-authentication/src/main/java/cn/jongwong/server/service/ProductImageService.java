package cn.jongwong.server.service;

import cn.jongwong.server.entity.ProductImageVO;
import cn.jongwong.server.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductImageService {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductImageRepository productImageRepository;

    // 根据商品ID和图片URL查找图片
    public Mono<ProductImageVO> findByProductIdAndUrl(String productId, String url) {
        return productImageRepository.findByProductIdAndUrl(productId, url);
    }

    public Mono<ProductImageVO> save(ProductImageVO image, String productId) {
        // 如果图片没有关联的 productId，则设置 productId
        if (image.getProductId() == null) {
            image.setProductId(productId);
        }

        // 先获取当前用户 ID
        return userService.getCurrentUserReactiveId()
                .flatMap(userId -> {

                    // 查找是否已经存在相同的图片（根据商品ID和URL）
                    return productImageRepository.findByProductIdAndUrl(productId, image.getUrl())
                            .flatMap(existingImage -> {
                                // 如果存在，则进行数据合并（merge）
                                mergeProductImage(existingImage, image);

                                // 更新ID以便进行更新操作
                                image.setId(existingImage.getId());

                                // 保存更新后的图片
                                return productImageRepository.save(image);
                            })
                            .switchIfEmpty(
                                    // 如果不存在，则插入新的图片
                                    productImageRepository.save(image)
                            );
                });
    }

    // 合并已有图片与新图片的字段
    private void mergeProductImage(ProductImageVO existingImage, ProductImageVO newImage) {
        // 合并相同的字段
        if (newImage.getName() != null) {
            existingImage.setName(newImage.getName()); // 合并名称
        }
        if (newImage.getUrl() != null) {
            existingImage.setUrl(newImage.getUrl()); // 合并URL
        }
        if (newImage.getImageType() != null) {
            existingImage.setImageType(newImage.getImageType()); // 合并图片类型
        }
        // 其他字段合并的逻辑根据需要进行扩展
    }
}
