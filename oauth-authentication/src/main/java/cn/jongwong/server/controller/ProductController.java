package cn.jongwong.server.controller;

import cn.jongwong.server.dto.product.CommonRejectDTO;
import cn.jongwong.server.entity.ProductVO;
import cn.jongwong.server.service.ProductService;
import cn.jongwong.server.util.response.PageResponse;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    // 创建商品
    @PostMapping
    public Mono<ProductVO> createUser(@RequestBody ProductVO productVO) {
        return productService.createProduct(productVO);
    }

    @GetMapping
    public Mono<PageResponse<ProductVO>> getUserList(@RequestParam(required = false) String name,
                                                     @RequestParam(required = true) int page,
                                                     @RequestParam(required = true) int size) {
        return PageResponse.reactivePageSuccess(productService.getProductList(name, page, size));
    }

    // 更新商品
    @PutMapping("/{id}")
    public Mono<ProductVO> update(@PathVariable String id, @RequestBody ProductVO productVO) {
        productVO.setId(id);
        return productService.saveDraftProduct(id, productVO);
    }


    // 获取商品详情
    @GetMapping("/{id}")
    public Mono<Response<ProductVO>> getProductDetail(@PathVariable String id) {
        return productService.getProductWithImagesById(id)
                .map(Response::success) // 成功时返回响应
                .switchIfEmpty(Mono.just(Response.error("找不到商品"))); // 如果找不到商品，返回错误
    }


    // 删除商品
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return productService.deleteProduct(id);
    }


    // 提交审核
    @PutMapping("/{productId}/submit")
    public Mono<ResponseEntity<ProductVO>> submitForReview(@PathVariable String productId, @RequestBody ProductVO productVO) {
        return productService.submit(productId, productVO)
                .map(newProductVO -> ResponseEntity.ok(newProductVO))  // 成功返回 HTTP 200 和产品信息
                .defaultIfEmpty(ResponseEntity.notFound().build());  // 如果找不到产品，返回 404
    }

    // 审核通过
    @PutMapping("/{productId}/approve")
    public Mono<ResponseEntity<ProductVO>> approveProduct(@PathVariable String productId) {
        return productService.approveProduct(productId)
                .map(productVO -> ResponseEntity.ok(productVO))  // 成功返回 HTTP 200 和产品信息
                .defaultIfEmpty(ResponseEntity.notFound().build());  // 如果找不到产品，返回 404
    }

    // 审核拒绝
    @PutMapping("/{productId}/reject")
    public Mono<ResponseEntity<ProductVO>> rejectProduct(@PathVariable String productId, @RequestBody CommonRejectDTO rejectRequest) {
        return productService.rejectProduct(productId, rejectRequest.getRejectionReason())
                .map(productVO -> ResponseEntity.ok(productVO))  // 成功返回 HTTP 200 和产品信息
                .defaultIfEmpty(ResponseEntity.notFound().build());  // 如果找不到产品，返回 404
    }

}
