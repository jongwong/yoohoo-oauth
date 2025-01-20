package cn.jongwong.server.controller;

import cn.jongwong.server.entity.ClientPurchaseGroupProductVO;
import cn.jongwong.server.entity.PurchaseGroupVO;
import cn.jongwong.server.service.PurchaseGroupService;
import cn.jongwong.server.service.product.PurchaseGroupProductService;
import cn.jongwong.server.util.response.PageResponse;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/purchase-group")
public class PurchaseGroupController {

    @Autowired
    private PurchaseGroupService purchaseGroupService;


    @Autowired
    private PurchaseGroupProductService purchaseGroupProductService;


    @GetMapping
    public Mono<PageResponse<PurchaseGroupVO>> search(@RequestParam(required = false) String name,
                                                      @RequestParam(required = false) Integer enable,
                                                      @RequestParam(required = true) int page,
                                                      @RequestParam(required = true) int size) {
        return PageResponse.reactivePageSuccess(purchaseGroupService.search(name, enable, page, size));
    }


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
    public Mono<Response<PurchaseGroupVO>> update(@PathVariable String id, @RequestBody PurchaseGroupVO data) {

        data.setId(id);
        return purchaseGroupService.update(data)
                .map(Response::ok)
                .defaultIfEmpty(Response.notFound());
    }

    @DeleteMapping("/{id}")
    public Mono<Response<Void>> delete(@PathVariable String id) {
        return purchaseGroupService.deleteById(id)
                .then(Mono.just(Response.success()));
    }

//
//         补充下面全部参数   String productName, String categoryId, Integer groupStatus, Integer listedStatus, Integer enable,
//            String timeDeliveryStart, String timeDeliveryEnd,


    @GetMapping("/product")
    public Mono<PageResponse<ClientPurchaseGroupProductVO>> queryProduct(

            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String distributionPointId,
            @RequestParam(required = false) Integer[] groupStatus,
            @RequestParam(required = false) Integer listedStatus,
            @RequestParam(required = false) Integer enable,
            @RequestParam(required = false) LocalDateTime timeDeliveryStart,
            @RequestParam(required = false) LocalDateTime timeDeliveryEnd,
            @RequestParam(required = false) LocalDateTime timeGroupStart,
            @RequestParam(required = false) LocalDateTime timeGroupEnd,
            @RequestParam int page,
            @RequestParam int size) {
        return purchaseGroupProductService.search(productName, categoryId, distributionPointId, groupStatus, listedStatus, enable,
                        timeDeliveryStart, timeDeliveryEnd, timeGroupStart, timeGroupEnd, page, size)
                .map(PageResponse::success);

    }
}
