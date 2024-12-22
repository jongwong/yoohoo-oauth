package cn.jongwong.server.controller;

import cn.jongwong.server.entity.Product;
import cn.jongwong.server.service.ProductService;
import cn.jongwong.server.util.response.PageResponse;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    // 创建商品
    @PostMapping
    public Mono<Product> createUser(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @GetMapping
    public Mono<PageResponse<Product>> getUserList(@RequestParam(required = false) String name,
                                                   @RequestParam(required = true) int page,
                                                   @RequestParam(required = true) int size) {
        return PageResponse.reactivePageSuccess(productService.getProductList(name, page, size));
    }

    // 更新商品
    @PutMapping("/{id}")
    public Mono<Product> update(@PathVariable String id, @RequestBody Product product) {
        product.setId(id);
        return productService.updateProduct(id, product);
    }


    // 获取商品详情
    @GetMapping("/{id}")
    public Mono<Response<Product>> getProductDetail(@PathVariable String id) {
        return productService.getProductWithImagesById(id)
                .map(Response::success) // 成功时返回响应
                .switchIfEmpty(Mono.just(Response.error("找不到商品"))); // 如果找不到商品，返回错误
    }


    // 删除商品
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return productService.deleteProduct(id);
    }


}
