package cn.jongwong.server.controller;

import cn.jongwong.server.entity.UserCouponsVO;
import cn.jongwong.server.service.UserCouponsService;
import cn.jongwong.server.util.response.PageResponse;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin/user-coupons")
public class UserCouponsController {

    @Autowired
    private UserCouponsService userCouponsService;


    // 根据 ID 获取用户优惠券
    @GetMapping("/{id}")
    public Mono<Response<UserCouponsVO>> getDataById(@PathVariable String id) {
        return userCouponsService.findById(id)
                .map(Response::success)
                .defaultIfEmpty(Response.error("找不到用户优惠券"));
    }

    // 创建用户优惠券
    @PostMapping
    public Mono<Response<UserCouponsVO>> create(@RequestBody UserCouponsVO userCouponsVO) {
        return userCouponsService.createUserCoupon(userCouponsVO)
                .map(Response::success);
    }


    // 删除用户优惠券
    @DeleteMapping("/{id}")
    public Mono<Response<String>> delete(@PathVariable String id) {
        return userCouponsService.delete(id)
                .map(Response::success);
    }

    // 标记优惠券为已使用
    @PutMapping("/{id}/use")
    public Mono<Response<UserCouponsVO>> markAsUsed(@PathVariable String id) {
        return userCouponsService.markAsUsed(id)
                .map(Response::success)
                .defaultIfEmpty(Response.notFound());
    }


    @GetMapping("/by-coupon/{couponId}")
    public Mono<PageResponse<UserCouponsVO>> searchByCouponId(
            @PathVariable String couponId,
            @RequestParam(required = true) int page,
            @RequestParam(required = true) int size) {
        return PageResponse.reactivePageSuccess(userCouponsService.searchUserByCouponsId(couponId, page, size));
    }

    @GetMapping("/by-user/{userId}")
    public Mono<PageResponse<UserCouponsVO>> searchByUserId(
            @PathVariable String userId,
            @RequestParam(required = true) int page,
            @RequestParam(required = true) int size) {
        return PageResponse.reactivePageSuccess(userCouponsService.searchUserByCouponsId(userId, page, size));
    }


}
