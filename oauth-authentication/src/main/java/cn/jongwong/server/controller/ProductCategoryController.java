package cn.jongwong.server.controller;


import cn.jongwong.server.dto.product.CommonBatchDTO;
import cn.jongwong.server.entity.ProductCategoryVO;
import cn.jongwong.server.service.product.ProductCategoryService;
import cn.jongwong.server.util.response.PageResponse;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/admin/product-category")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    // 根据ID查找商品类别
    @GetMapping("/{id}")
    public Mono<Response<ProductCategoryVO>> findById(@PathVariable String id) {
        return productCategoryService.findById(id)
                .map(Response::success)
                .defaultIfEmpty(Response.notFound());
    }

    // 分页查询商品类别
    @GetMapping
    public Mono<PageResponse<ProductCategoryVO>> search(@RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String code,
                                                        @RequestParam(required = false) Integer level,
                                                        @RequestParam(required = true) Integer page,
                                                        @RequestParam(required = true) Integer size) {
        return PageResponse.reactivePageSuccess(productCategoryService.search(name, code, level, page, size));
    }


    // 获取所有商品类别
    @GetMapping("all")
    public Mono<Response<ProductCategoryVO[]>> findAll() {
        return productCategoryService.findAll()
                .collectList() // 将 Flux 转换为 List
                .map(list -> list.toArray(new ProductCategoryVO[0])) // 转换为数组
                .map(Response::ok); // 包装为响应对象
    }

    // 创建商品类别
    @PostMapping
    public Mono<Response<ProductCategoryVO>> create(@RequestBody ProductCategoryVO productCategoryVO) {
        return productCategoryService.create(productCategoryVO)
                .map(Response::success);
    }

    // 更新商品类别
    @PutMapping("/{id}")
    public Mono<Response<ProductCategoryVO>> update(@PathVariable String id, @RequestBody ProductCategoryVO productCategoryVO) {
        productCategoryVO.setId(id);
        return productCategoryService.update(productCategoryVO)
                .map(Response::ok)
                .defaultIfEmpty(Response.notFound());
    }

    // 启用商品类别
    @PutMapping("/{id}/enable")
    public Mono<Response<ProductCategoryVO>> enable(@PathVariable String id) {
        return productCategoryService.enable(id)
                .map(Response::ok)
                .defaultIfEmpty(Response.notFound());
    }

    // 停用商品类别
    @PutMapping("/{id}/disable")
    public Mono<Response<ProductCategoryVO>> disable(@PathVariable String id) {
        return productCategoryService.disable(id)
                .map(Response::ok)
                .defaultIfEmpty(Response.notFound());
    }

    // 删除商品类别
    @DeleteMapping("/{id}")
    public Mono<Response<Void>> delete(@PathVariable String id) {
        return productCategoryService.deleteById(id)
                .then(Mono.just(Response.success()));  // 删除成功后返回一个空的响应
    }

    @PostMapping("/batch")
    public Mono<Response<List<ProductCategoryVO>>> getProductListByIds(@RequestBody CommonBatchDTO data) {
        return productCategoryService.findByIds(data.getIds()).collectList().map(Response::success);
    }
}
