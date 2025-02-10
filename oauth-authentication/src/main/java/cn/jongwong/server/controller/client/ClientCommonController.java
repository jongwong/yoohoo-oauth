package cn.jongwong.server.controller.client;


import cn.jongwong.server.dto.order.OrderSubmitDTO;
import cn.jongwong.server.entity.OrderVO;
import cn.jongwong.server.service.OrderService;
import cn.jongwong.server.service.UserService;
import cn.jongwong.server.util.response.PageResponse;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("/client")
public class ClientCommonController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;


    // 获取最近的配送点
    @GetMapping("/delivery/fee")
    public Mono<Response<Double>> getDeliveryFee() {
        var fee = 1.5;
        // String 转成 浮点数
        return Mono.just(fee).map(Response::ok);
    }


    @PostMapping("/order/submit")
    public Mono<Response<OrderVO>> submitOrder(@RequestBody OrderSubmitDTO data) {
        return orderService.submit(data).map(Response::ok);
    }

    @GetMapping("/order/user")
    public Mono<PageResponse<OrderVO>> queryByUser() {
        return userService.getCurrentUser().flatMap(u -> orderService.queryByUserId(1, 10, u.getId())).map(PageResponse::success);
    }


}
