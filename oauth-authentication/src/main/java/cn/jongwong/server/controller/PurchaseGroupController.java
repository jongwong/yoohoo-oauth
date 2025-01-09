package cn.jongwong.server.controller;

import cn.jongwong.server.entity.PurchaseGroupVO;
import cn.jongwong.server.service.product.PurchaseGroupService;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin/purchase-group")
public class PurchaseGroupController {

    @Autowired
    private PurchaseGroupService purchaseGroupService;

    @GetMapping("/{id}")
    public Mono<Response<PurchaseGroupVO>> findById(@PathVariable String id) {
        return purchaseGroupService.findById(id)
                .map(Response::success)
                .defaultIfEmpty(Response.notFound());
    }

    @PostMapping
    public Mono<Response<PurchaseGroupVO>> create(@RequestBody PurchaseGroupVO purchaseGroupVO) {
        return purchaseGroupService.create(purchaseGroupVO)
                .map(Response::success);
    }

    @PutMapping("/{id}")
    public Mono<Response<PurchaseGroupVO>> update(@PathVariable String id, @RequestBody PurchaseGroupVO purchaseGroupVO) {
        purchaseGroupVO.setId(id);
        return purchaseGroupService.update(purchaseGroupVO)
                .map(Response::ok)
                .defaultIfEmpty(Response.notFound());
    }

    @DeleteMapping("/{id}")
    public Mono<Response<Void>> delete(@PathVariable String id) {
        return purchaseGroupService.deleteById(id)
                .then(Mono.just(Response.success()));
    }
}
