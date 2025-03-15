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

@RestController()
@RequestMapping("/client/admin/group")
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


    @GetMapping("/permission/check")
    public Mono<Response<Boolean>> refundOrder() {
        return userService.getCurrentUserReactive().flatMap(u -> groupAdminService.getAdminsByUserId(u.getId()).collectList().map(admins -> {
            if (!admins.isEmpty()) {
                return Response.ok(true);
            } else {
                return Response.ok(false);
            }
        }));
    }

    @GetMapping
    public Mono<PageResponse<PurchaseGroupVO>> searchGroup(@RequestParam(required = false) String name,
                                                           @RequestParam(required = false) Integer enable,
                                                           @RequestParam(required = true) int page,
                                                           @RequestParam(required = true) int size) {
        return purchaseGroupService.clientSearch(name, enable, page, size).map(PageResponse::success);


    }


    @GetMapping("/{id}")
    public Mono<Response<PurchaseGroupVO>> findById(@PathVariable String id) {
        return purchaseGroupService.findById(id)
                .map(Response::success)
                .defaultIfEmpty(Response.notFound());
    }


}
