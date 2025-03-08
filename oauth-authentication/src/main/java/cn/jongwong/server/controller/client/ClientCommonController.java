package cn.jongwong.server.controller.client;


import cn.jongwong.server.config.wechatpay.WeChatPayService;
import cn.jongwong.server.dto.order.OrderPayDTO;
import cn.jongwong.server.dto.order.OrderRefundDTO;
import cn.jongwong.server.dto.order.OrderSubmitDTO;
import cn.jongwong.server.entity.OrderVO;
import cn.jongwong.server.service.OrderService;
import cn.jongwong.server.service.UserService;
import cn.jongwong.server.util.response.PageResponse;
import cn.jongwong.server.util.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wechat.pay.java.core.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController()
@RequestMapping("/client")
public class ClientCommonController {


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;


    @Autowired
    private WeChatPayService weChatPayService;


    // 获取最近的配送点
    @GetMapping("/delivery/fee")
    public Mono<Response<Double>> getDeliveryFee() {
        var fee = 0.0;
        // String 转成 浮点数
        return Mono.just(fee).map(Response::ok);
    }


    @PostMapping("/order/submit")
    public Mono<Response<OrderVO>> submitOrder(@RequestBody OrderSubmitDTO data) {
        return orderService.submit(data).map(Response::ok);
    }

    @PostMapping("/order/pay/submit")
    public Mono<Response<OrderVO>> submitPay(@RequestBody OrderPayDTO data) {
        return orderService.payOrder(data).map(Response::ok);
    }

    @GetMapping("/order/user")
    public Mono<PageResponse<OrderVO>> queryByUser(@RequestParam(required = false) Integer status) {
        return userService.getCurrentUserReactive().flatMap(u -> orderService.queryByUserId(1, 10, u.getId(), status)).map(PageResponse::success);
    }

    @GetMapping("/order/{id}")
    public Mono<Response<OrderVO>> queryByUser(@PathVariable(required = true) String id) {
        return userService.getCurrentUserReactive().flatMap(u -> orderService.findOneByOrderId(id).map(Response::success));
    }

    @PostMapping("/order/{id}/cancel")
    public Mono<Response<OrderVO>> cancelOrder(@PathVariable(required = true) String id) {
        return orderService.cancelById(id).map(Response::ok);
    }

    @PostMapping("/wechat-pay/refund/notify")
    public Mono<ResponseEntity<?>> refundNotify(
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Nonce") String nonce,
            @RequestBody String body
    ) {

        return weChatPayService.validateSignature(signature, timestamp, nonce, body)
                .flatMap(isValid -> {
                    if (isValid) {
                        try {
                            var callback = objectMapper.readValue(body, Notification.class);

                            var source = callback.getResource();
                            var reStr = weChatPayService.handlePaymentCallback(source.getCiphertext(), source.getAssociatedData(), source.getNonce());
                            var result = objectMapper.readValue(reStr, Map.class);
                            String transactionId = (String) result.get("transaction_id");

                            String outTradeNo = (String) result.get("out_trade_no");

                            var tradeState = result.get("refund_status");
                            if (tradeState.equals("SUCCESS")) {
                                // 使用 split 方法分割字符串
                                String[] parts = outTradeNo.split("-");

                                // 获取分割后的第一个部分
                                String num = parts[0];
                                return orderService.finishRefund(num, outTradeNo, transactionId).map(ResponseEntity::ok);

                            }

                            return Mono.error(new Exception("Payment failed."));

                        } catch (Exception e) {
                            e.printStackTrace();
                            return Mono.error(e);
                        }

//
                    }
                    var re = ResponseEntity.status(400).body("Verification failed.");
                    return Mono.just(re);
                })
                .onErrorReturn(ResponseEntity.status(500).body("Verification error."));
    }


    @PostMapping("/wechat-pay/payment/notify")
    public Mono<ResponseEntity<?>> paymentNotify(
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Nonce") String nonce,
            @RequestBody String body
    ) {
        return weChatPayService.validateSignature(signature, timestamp, nonce, body)
                .flatMap(isValid -> {
                    if (isValid) {
                        try {
                            var callback = objectMapper.readValue(body, Notification.class);

                            var source = callback.getResource();
                            var reStr = weChatPayService.handlePaymentCallback(source.getCiphertext(), source.getAssociatedData(), source.getNonce());

                            var result = objectMapper.readValue(reStr, Map.class);
                            String transactionId = (String) result.get("transaction_id");

                            String outTradeNo = (String) result.get("out_trade_no");
                            var tradeState = result.get("trade_state");
                            if (tradeState.equals("SUCCESS")) {
                                // 使用 split 方法分割字符串
                                String[] parts = outTradeNo.split("-");

                                // 获取分割后的第一个部分
                                String num = parts[0];
                                return orderService.finishPayment(num, outTradeNo, transactionId).map(ResponseEntity::ok);

                            }

                            return Mono.error(new Exception("Payment failed."));

                        } catch (Exception e) {
                            e.printStackTrace();
                            return Mono.error(e);
                        }

//
                    }
                    var re = ResponseEntity.status(400).body("Verification failed.");
                    return Mono.just(re);
                })
                .onErrorReturn(ResponseEntity.status(500).body("Verification error."));
    }

    @PostMapping("/order/refund")
    public Mono<Response<OrderVO>> refundOrder(@RequestBody OrderRefundDTO data) {
        return orderService.refund(data).map(Response::ok);
    }



}
