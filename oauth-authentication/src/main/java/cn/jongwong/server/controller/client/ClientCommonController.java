package cn.jongwong.server.controller.client;


import cn.jongwong.server.config.oss.OssService;
import cn.jongwong.server.config.wechatpay.WeChatPayService;
import cn.jongwong.server.dto.order.OrderPayDTO;
import cn.jongwong.server.dto.order.OrderRefundApproveDTO;
import cn.jongwong.server.dto.order.OrderRefundDTO;
import cn.jongwong.server.dto.order.OrderSubmitDTO;
import cn.jongwong.server.entity.DistributionPointVO;
import cn.jongwong.server.entity.OrderVO;
import cn.jongwong.server.entity.OrderWithInfoVO;
import cn.jongwong.server.service.DistributionPointService;
import cn.jongwong.server.service.GroupAdminService;
import cn.jongwong.server.service.OrderService;
import cn.jongwong.server.service.UserService;
import cn.jongwong.server.util.response.PageResponse;
import cn.jongwong.server.util.response.Response;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wechat.pay.java.core.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
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
    private GroupAdminService groupAdminService;

    @Autowired
    private WeChatPayService weChatPayService;


    @Autowired
    private OssService ossService;


    @Autowired
    private DistributionPointService distributionPointService;


    @PostMapping("/oss/upload")
    public Mono<Map<String, String>> upload(@RequestPart("file") Mono<FilePart> filePartMono,
                                            @RequestPart("path") String filePath) {

        // 1️⃣ 先异步获取临时凭证
        return Mono.fromSupplier(ossService::getTemporaryCredentials)
                .flatMap(credentials -> {
                    if (credentials == null || credentials.isEmpty()) {
                        return Mono.error(new RuntimeException("获取 OSS 临时凭证失败"));
                    }

                    // 解析 OSS 临时凭证
                    String bucketName = credentials.get("bucketName");
                    String region = credentials.get("region");
                    String accessKeyId = credentials.get("accessKeyId");
                    String accessKeySecret = credentials.get("accessKeySecret");
                    String securityToken = credentials.get("securityToken");
                    String host = credentials.get("host"); // 例如：https://yoohoo-oss.oss-cn-shanghai.aliyuncs.com

                    // 2️⃣ 处理文件上传
                    return filePartMono.flatMap(filePart ->
                            DataBufferUtils.join(filePart.content()) // 直接获取完整数据
                                    .flatMap(buffer -> Mono.fromCallable(() -> {
                                        // 3️⃣ 上传文件到 OSS
                                        try (InputStream inputStream = buffer.asInputStream()) {
                                            OSS ossClient = new OSSClientBuilder().build(
                                                    "oss-" + region + ".aliyuncs.com",
                                                    accessKeyId,
                                                    accessKeySecret,
                                                    securityToken
                                            );
                                            ossClient.putObject(bucketName, filePath, inputStream);
                                            ossClient.shutdown(); // 关闭客户端

                                            // 4️⃣ 生成 URL 并返回
                                            String fileUrl = host + "/" + filePath;
                                            Map<String, String> response = new HashMap<>();
                                            response.put("url", fileUrl);
                                            response.put("fileName", filePath);
                                            return response;
                                        }
                                    }))
                                    .subscribeOn(Schedulers.boundedElastic()) // 让上传操作在独立线程池执行
                    );
                });
    }
    // 获取最近的配送点
    @GetMapping("/delivery/fee")
    public Mono<Response<Integer>> getDeliveryFee(@RequestParam(required = false) String pointId) {
        return distributionPointService.findById(pointId).map(DistributionPointVO::getAmountDelivery).map(Response::ok);
    }


    @PostMapping("/order/submit")
    public Mono<Response<OrderVO>> submitOrder(@RequestBody OrderSubmitDTO data) {
        return orderService.submit(data).map(Response::ok);
    }

    @PostMapping("/order/pay/submit")
    public Mono<Response<Map<String, String>>> submitPay(@RequestBody OrderPayDTO data) {
        return orderService.payOrder(data).map(Response::ok);
    }

    @GetMapping("/order/user")
    public Mono<PageResponse<OrderVO>> queryByUser(@RequestParam(required = false) Integer status) {
        return userService.getCurrentUserReactive().flatMap(u -> orderService.queryByUserId(1, 10, u.getId(), status)).map(PageResponse::success);
    }

    @GetMapping("/group/order")
    public Mono<Response<List<OrderVO>>> queryByGroupId(@RequestParam(required = false) String groupId) {
        return orderService.findAllByGroupId(groupId).map(Response::success);
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


    @GetMapping("/admin/order")
    public Mono<PageResponse<OrderVO>> queryByUser(@RequestParam(required = true) Integer page, @RequestParam(required = true) Integer size, @RequestParam(required = false) Integer status) {
        return orderService.query(page, size, status).map(PageResponse::success);
    }

    @GetMapping("/admin/order/with_refund")
    public Mono<PageResponse<OrderWithInfoVO>> queryWithRefundByUser(@RequestParam(required = true) Integer page, @RequestParam(required = true) Integer size, @RequestParam(required = false) Integer[] status, @RequestParam(required = false) String groupId) {
        return orderService.queryWithPaymentRefundInfo(page, size, status, groupId).map(PageResponse::success);
    }


    @PostMapping("/order/refund")
    public Mono<Response<Boolean>> refundOrder(@RequestBody OrderRefundDTO data) {
        return orderService.refund(data).map(Response::ok);
    }


    @PostMapping("/order/refund/direct")
    public Mono<Response<OrderVO>> directRefund(@RequestBody OrderRefundDTO data) {
        return orderService.directRefund(data).map(Response::ok);
    }

    @PostMapping("/order/refund/approve")
    public Mono<Response<OrderVO>> refundApprove(@RequestBody OrderRefundApproveDTO data) {
        return orderService.refundApprove(data).map(Response::ok);
    }

    @PostMapping("/order/refund/reject")
    public Mono<Response<OrderVO>> refundReject(@RequestBody OrderRefundApproveDTO data) {
        return orderService.refundApproveReject(data).map(Response::ok);
    }



}
