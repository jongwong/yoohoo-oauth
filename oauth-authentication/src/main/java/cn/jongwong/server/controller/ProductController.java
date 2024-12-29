package cn.jongwong.server.controller;

import cn.jongwong.server.dto.product.CommonRejectDTO;
import cn.jongwong.server.entity.ProductVO;
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
    public Mono<ProductVO> create(@RequestBody ProductVO productVO) {
        return productService.create(productVO);
    }

    @GetMapping
    public Mono<PageResponse<ProductVO>> search(@RequestParam(required = false) String name,
                                                @RequestParam(required = false) String archivedStatus,
                                                @RequestParam(required = true) int page,
                                                @RequestParam(required = true) int size) {
        return PageResponse.reactivePageSuccess(productService.search(name, archivedStatus, page, size));
    }

    // 更新商品
    @PutMapping("/{id}")
    public Mono<Response<ProductVO>> update(@PathVariable String id, @RequestBody ProductVO productVO) {
        productVO.setId(id);
        return productService.update(id, productVO).map(Response::success) // 成功时返回响应
                .switchIfEmpty(Mono.just(Response.error("找不到商品"))); // 如果找不到商品，返回错误
    }


    // 获取商品详情
    @GetMapping("/{id}")
    public Mono<Response<ProductVO>> getProductDetail(@PathVariable String id) {
        return productService.getProductWithImagesById(id)
                .map(Response::success) // 成功时返回响应
                .switchIfEmpty(Mono.just(Response.error("找不到商品"))); // 如果找不到商品，返回错误
    }




    // 提交审核
    @PutMapping("/{productId}/submit")
    public Mono<Response<ProductVO>> submitForReview(@PathVariable String productId, @RequestBody ProductVO productVO) {
        return productService.submit(productId, productVO)
                .map(Response::success)
                .switchIfEmpty(Mono.just(Response.error("找不到商品")));  // 如果找不到产品，返回 404
    }

    // 审核通过
    @PutMapping("/{productId}/approve")
    public Mono<Response<ProductVO>> approveProduct(@PathVariable String productId) {
        return productService.approve(productId)
                .map(Response::success)
                .switchIfEmpty(Mono.just(Response.error("找不到商品")));
    }

    // 审核拒绝
    @PutMapping("/{productId}/reject")
    public Mono<Response<ProductVO>> rejectProduct(@PathVariable String productId, @RequestBody CommonRejectDTO rejectRequest) {
        return productService.reject(productId, rejectRequest.getRejectionReason())
                .map(Response::success)
                .switchIfEmpty(Mono.just(Response.error("找不到商品")));
    }

    @DeleteMapping("/{id}")
    public Mono<Response<String>> delete(@PathVariable String id) {
        return productService.delete(id)
                .map(Response::success);
    }

}
