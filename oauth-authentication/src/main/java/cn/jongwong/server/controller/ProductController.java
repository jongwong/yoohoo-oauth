package cn.jongwong.server.controller;

import cn.jongwong.server.entity.Product;
import cn.jongwong.server.service.ProductService;
import cn.jongwong.server.util.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    // 创建用户
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

    // 更新用户
    @PutMapping("/{id}")
    public Mono<Product> updateUser(@PathVariable String id, @RequestBody Product product) {
        product.setId(id);
        return productService.updateProduct(id, product);
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable String id) {
        return productService.deleteProduct(id);
    }


}
