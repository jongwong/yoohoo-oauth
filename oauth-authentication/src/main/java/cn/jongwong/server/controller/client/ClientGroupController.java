package cn.jongwong.server.controller.client;


import cn.jongwong.server.config.wechatpay.WeChatPayService;
import cn.jongwong.server.entity.PurchaseGroupVO;
import cn.jongwong.server.service.*;
import cn.jongwong.server.util.response.PageResponse;
import cn.jongwong.server.util.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController()
@RequestMapping("/client")
public class ClientGroupController {


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupAdminService groupAdminService;

    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private ProductService productService;

    @Autowired
    private PurchaseGroupService purchaseGroupService;


    @GetMapping("/admin/group/permission/check")
    public Mono<Response<Boolean>> refundOrder() {
        return userService.getCurrentUserReactive().flatMap(u -> groupAdminService.getAdminsByUserId(u.getId()).collectList().map(admins -> {
            if (!admins.isEmpty()) {
                return Response.ok(true);
            } else {
                return Response.ok(false);
            }
        }));
    }


    @GetMapping("/group/{id}")
    public Mono<Response<PurchaseGroupVO>> findById(@PathVariable String id) {
        return purchaseGroupService.findById(id)
                .map(Response::success)
                .defaultIfEmpty(Response.notFound());
    }

    @GetMapping("/admin/group")
    public Mono<PageResponse<PurchaseGroupVO>> searchGroup(@RequestParam(required = false) String name,
                                                           @RequestParam(required = false) Integer enable,
                                                           @RequestParam(required = true) int page,
                                                           @RequestParam(required = true) int size) {
        return purchaseGroupService.clientAdminSearch(name, enable, page, size).map(PageResponse::success);


    }

    @GetMapping("/admin/group/{id}")
    public Mono<Response<PurchaseGroupVO>> findAdminById(@PathVariable String id) {
        return purchaseGroupService.findById(id)
                .map(Response::success)
                .defaultIfEmpty(Response.notFound());
    }

    @GetMapping("/group")
    public Mono<PageResponse<PurchaseGroupVO>> searchClientGroup(
            @RequestParam(required = false) String distributionPointId,
            @RequestParam(required = false) Integer[] groupStatus,
            @RequestParam(required = false) LocalDateTime timeDeliveryStart,
            @RequestParam(required = false) LocalDateTime timeDeliveryEnd,
            @RequestParam(required = false) LocalDateTime timeGroupStart,
            @RequestParam(required = false) LocalDateTime timeGroupEnd,
            @RequestParam int page, // 当前页
            @RequestParam int size) { // 每页大小) {
        return purchaseGroupService.clientSearch(distributionPointId, groupStatus, timeDeliveryStart, timeDeliveryEnd, timeGroupStart, timeGroupEnd, page, size).map(PageResponse::success);


    }


}
