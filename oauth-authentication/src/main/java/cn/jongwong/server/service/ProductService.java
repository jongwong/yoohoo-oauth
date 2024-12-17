package cn.jongwong.server.service;

import cn.jongwong.server.common.MapperUtil;
import cn.jongwong.server.common.QueryBuilder;
import cn.jongwong.server.entity.Product;
import cn.jongwong.server.repository.ProductRepository;
import cn.jongwong.server.util.response.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    R2dbcEntityTemplate r2dbcEntityTemplate;


    // 创建商品
    public Mono<Product> createProduct(Product product) {
        return productRepository.save(product); // 保存商品到数据库
    }

    // 根据ID查询商品
    public Mono<Product> getProductById(String id) {
        return productRepository.findById(id); // 根据ID查询商品
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
