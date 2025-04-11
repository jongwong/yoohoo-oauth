package cn.jongwong.server.controller;

import cn.jongwong.server.dto.product.CommonBatchDTO;
import cn.jongwong.server.dto.product.CommonRejectDTO;
import cn.jongwong.server.entity.CouponsVO;
import cn.jongwong.server.entity.UserCouponsVO;
import cn.jongwong.server.service.CouponsService;
import cn.jongwong.server.service.UserCouponsService;
import cn.jongwong.server.util.response.PageResponse;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/admin/coupons")
public class CouponsController {

    @Autowired
    private CouponsService couponsService;

    @Autowired
    private UserCouponsService userCouponsService;




    @GetMapping
    public Mono<PageResponse<CouponsVO>> search(@RequestParam(required = false) String name,
                                                @RequestParam(required = false) String status,
                                                @RequestParam(required = true) int page,
                                                @RequestParam(required = true) int size) {
        return PageResponse.reactivePageSuccess(couponsService.search(name, status, page, size));
    }


    // 获取优惠券通过ID
    @GetMapping("/{id}")
    public Mono<Response<CouponsVO>> getDataById(@PathVariable String id) {
        return couponsService.findById(id)
                .map(Response::success)
                .defaultIfEmpty(Response.error("找不到优惠券"));
    }

    // 创建优惠券
    @PostMapping
    public Mono<Response<CouponsVO>> create(@RequestBody CouponsVO couponsVO) {
        return couponsService.createCoupon(couponsVO)
                .map(Response::success);
    }

    // 更新优惠券
    @PutMapping("/{id}")
    public Mono<Response<CouponsVO>> update(@PathVariable String id, @RequestBody CouponsVO couponsVO) {
        return couponsService.update(id, couponsVO)
                .map(Response::success)
                .defaultIfEmpty(Response.notFound());
    }

    @DeleteMapping("/{id}")
    public Mono<Response<String>> delete(@PathVariable String id) {
        return couponsService.delete(id)
                .map(Response::success);
    }

    @PutMapping("/{id}/submit")
    public Mono<Response<CouponsVO>> submit(@PathVariable String id, @RequestBody CouponsVO couponsVO) {
        return couponsService.submit(id, couponsVO)
                .map(Response::success);
    }

    @PutMapping("/{id}/reject")
    public Mono<Response<CouponsVO>> reject(@PathVariable String id, @RequestBody CommonRejectDTO rejectRequest) {
        return couponsService.reject(id, rejectRequest.getRejectionReason())
                .map(Response::success)
                .defaultIfEmpty(Response.notFound());
    }

    @PutMapping("/{id}/approve")
    public Mono<Response<CouponsVO>> approve(@PathVariable String id, @RequestBody CommonRejectDTO rejectRequest) {
        return couponsService.approve(id)
                .map(Response::success)
                .defaultIfEmpty(Response.notFound());
    }


    @PostMapping("/{id}/issue")
    public Mono<Response<List<UserCouponsVO>>> issueByUserIds(@PathVariable String id, @RequestBody CommonBatchDTO data) {
        return userCouponsService.issue(data.getIds(), id).collectList()
                .map(Response::success);
    }
}
