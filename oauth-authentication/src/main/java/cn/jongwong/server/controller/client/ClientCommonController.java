package cn.jongwong.server.controller.client;


import cn.jongwong.server.config.wechatpay.WxPayService;
import cn.jongwong.server.dto.order.OrderSubmitDTO;
import cn.jongwong.server.entity.OrderVO;
import cn.jongwong.server.service.OrderService;
import cn.jongwong.server.service.UserService;
import cn.jongwong.server.util.response.PageResponse;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("/client")
public class ClientCommonController {

    @Autowired
    private WxPayService wxPayService;

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
    public Mono<PageResponse<OrderVO>> queryByUser(@RequestParam(required = false) Integer status) {
        return userService.getCurrentUser().flatMap(u -> orderService.queryByUserId(1, 10, u.getId(), status)).map(PageResponse::success);
    }

    @GetMapping("/order/{id}")
    public Mono<Response<OrderVO>> queryByUser(@PathVariable(required = true) String id) {
        return userService.getCurrentUser().flatMap(u -> orderService.findOneByUserId(id).map(Response::success));
    }

    @PostMapping("/order/{id}/cancel")
    public Mono<Response<OrderVO>> cancelOrder(@PathVariable(required = true) String id) {
        return orderService.cancelById(id).map(Response::ok);
    }

    @PostMapping("/wechat-pay/notify/payment")
    public Flux<ResponseEntity<String>> paymentNotify(ServerWebExchange exchange) {
        return exchange.getRequest().getBody()
                .flatMap(dataBuffer -> {
                    // 将 DataBuffer 转换为字节数组
                    byte[] body = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(body);

                    // 将字节数组转为字符串（微信回调的 XML 数据）
                    String notifyData = new String(body);
                    System.out.printf("-------notifyData-------%s%n", notifyData);  // 可以换成更合适的日志记录

                    // 处理回调业务逻辑，比如签名验证等（这里简化了）
                    boolean isValid = validateNotifyData(notifyData); // 假设有一个验证签名的方法
                    if (!isValid) {
                        // 签名验证失败，返回失败响应
                        return Mono.just(ResponseEntity.status(400)
                                .body("<xml><return_code>FAIL</return_code><return_msg>签名失败</return_msg></xml>"));
                    }

                    // 根据回调内容判断支付结果
                    return Mono.just(ResponseEntity.ok("<xml><return_code>SUCCESS</return_code><return_msg>OK</return_msg></xml>"));
                });
    }

    private boolean validateNotifyData(String notifyData) {
        // 这里可以对接收到的数据进行签名验证，返回 true 或 false
        return true;
    }


}
