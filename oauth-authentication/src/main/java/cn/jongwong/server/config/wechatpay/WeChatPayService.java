package cn.jongwong.server.config.wechatpay;

import cn.jongwong.server.config.wechatpay.util.AesUtil;
import cn.jongwong.server.config.wechatpay.util.AuthorizationUtils;
import cn.jongwong.server.dto.order.OrderRefundDTO;
import cn.jongwong.server.entity.OrderVO;
import cn.jongwong.server.entity.PaymentVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static cn.jongwong.server.config.wechatpay.util.AuthorizationUtils.generatePaySign;


@Service
public class WeChatPayService {


    private static String mchId = "1704006353";
    private static String privateKeyPath = "/Users/jongwong/IdeaProjects/yoohoo-oauth/cert/apiclient_key.pem";
    private static String publicKeyPath = "/Users/jongwong/IdeaProjects/yoohoo-oauth/cert/public_key.pem";
    private static String merchantSerialNumber = "7E92F76242E500317FF50E2DE4F02C5D105A2853";
    private static String apiKey = "p96vNItUlqiccUB3dbLyxoiRYQW0fy7E";
    private static String certPath = "/Users/jongwong/IdeaProjects/yoohoo-oauth/cert/apiclient_cert.pem";
    private static String appId = "wx4b90fea0e7b2a714";
    private static String publicKeyId = "PUB_KEY_ID_0117040063532025021400298900001527";
    private static String notifyUrl = "https://local.c.api.yoohoo.cn/client/wechat-pay/payment/notify";
    private static String openId = "owoZV7KyzmktjTlKSqiR1Ama5aYg";



    @Autowired
    private ObjectMapper objectMapper;

    public static String generateOutTradeNo(String orderId) {
        int randomNum = 10000 + new Random().nextInt(90000); // 生成5位随机数
        return orderId + "-R" + randomNum;
    }

    public Mono<HashMap<String, String>> createJsApiOrder(String curOpenId, OrderVO order) {
        var tradeNum = generateOutTradeNo(order.getNum());
        // 创建请求参数
        Map<String, Object> request = new HashMap<>();
        request.put("appid", appId);
        request.put("mchid", mchId);

        request.put("description", "Image形象店-深圳腾大-QQ公仔");
        request.put("out_trade_no", tradeNum);
        request.put("time_expire", "2018-06-08T10:34:56+08:00");
//        request.put("attach", "{\"order_id\":\"" + oder.getId() + "\"}");
        request.put("notify_url", notifyUrl);
        request.put("goods_tag", "WXG");
        request.put("support_fapiao", false);
        request.put("amount", Map.of("total", 1, "currency", "CNY"));
        request.put("payer", Map.of("openid", curOpenId));
        request.put("detail", Map.of(
                "invoice_id", "微信123",
                "goods_detail", new Object[]{
                        Map.of(
                                "merchant_goods_id", "1246464644",
                                "wechatpay_goods_id", "1001",
                                "goods_name", "iPhoneX 256G",
                                "quantity", 1,
                                "unit_price", 528800
                        )
                }
        ));
        request.put("scene_info", Map.of(
                "payer_client_ip", "14.23.150.211",
                "device_id", "013467007045764",
                "store_info", Map.of(
                        "id", "0001",
                        "name", "腾讯大厦分店",
                        "area_code", "440305",
                        "address", "广东省深圳市南山区科技中一道10000号"
                )
        ));
        request.put("settle_info", Map.of("profit_sharing", false));
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.mch.weixin.qq.com")  // 微信支付 API 基础 URL
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();


        var authorizationInfo = new HashMap<String, String>();
        try {
            var body = objectMapper.writeValueAsString(request);
            ;
            var privateKeyPem = readPrivateKeyFromFile(privateKeyPath);
            authorizationInfo = AuthorizationUtils.buildAuthorizationInfo(mchId, privateKeyPem, merchantSerialNumber, "POST", "/v3/pay/transactions/jsapi", body);

        } catch (Exception e) {
            e.printStackTrace();
        }



        // 设置请求头，使用微信支付 API 密钥进行身份验证
        String stringToSign = authorizationInfo.get("authorization");
        var map = new HashMap<String, String>();
        map.put("timestamp", authorizationInfo.get("timestamp"));
        map.put("nonce_str", authorizationInfo.get("nonce_str"));
        map.put("package", authorizationInfo.get("package")); // 这里 keyNumber 代表 prepay_id
        map.put("sign_type", authorizationInfo.get("sign_type")); // 或 "HMAC-SHA256"，请确认你的签名类型
        map.put("pay_sign", authorizationInfo.get("pay_sign"));

        return webClient.post()
                .uri("https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi")
                .header("Authorization", stringToSign)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .map(res -> {
                    var prepayId = res.get("prepay_id").toString();
                    map.put("package", "prepay_id=" + prepayId);

                    try {
                        var privateKeyPem = readPrivateKeyFromFile(privateKeyPath);
                        var paySign = generatePaySign(appId, map.get("timestamp"), map.get("nonce_str"), prepayId, privateKeyPem);
                        map.put("pay_sign", paySign);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return map;
                })
                .doOnError(error -> {
                    System.err.println("请求失败：" + error.getMessage());

                });

    }


    public Mono<HashMap<String, String>> refundJsApiOrder(OrderVO order, PaymentVO payment, OrderRefundDTO refundDTO) {
        var tradeNum = payment.getTransactionNo(); // 订单号
        var refundNum = generateOutTradeNo(order.getNum()); // 生成退款单号

        // 退款请求参数
        Map<String, Object> request = new HashMap<>();
        request.put("transaction_id", payment.getTransactionId());

        request.put("out_trade_no", tradeNum);  // 原支付订单号
        request.put("out_refund_no", refundNum); // 退款单号
        request.put("reason", refundDTO.getReason()); // 退款原因
        var refundAmount = order.getAmountTotal();


        Map<String, Object> amount = new HashMap<>();
        amount.put("refund", refundAmount); // 退款金额（分）
        amount.put("total", refundAmount);  // 订单总金额（分）
        amount.put("currency", "CNY");
        request.put("amount", amount);
        request.put("notify_url", "https://local.c.api.yoohoo.cn/client/wechat-pay/refund/notify"); // 退款回调

        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.mch.weixin.qq.com")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();

        var authorizationInfo = new HashMap<String, String>();

        try {
            var body = objectMapper.writeValueAsString(request);
            var privateKeyPem = readPrivateKeyFromFile(privateKeyPath);

            // 生成 v3 版本的 Authorization 签名
            authorizationInfo = AuthorizationUtils.buildAuthorizationInfo(
                    mchId, privateKeyPem, merchantSerialNumber, "POST", "/v3/refund/domestic/refunds", body
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        String stringToSign = authorizationInfo.get("authorization");
        var responseMap = new HashMap<String, String>();

        return webClient.post()
                .uri("/v3/refund/domestic/refunds") // 退款 API 地址
                .header("Authorization", stringToSign)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .map(res -> {
                    responseMap.put("status", res.get("status").toString());
                    responseMap.put("refund_id", res.get("refund_id").toString());
                    responseMap.put("out_refund_no", refundNum);
                    responseMap.put("refund_amount", refundAmount.toString());

                    responseMap.put("transaction_id", res.get("transaction_id").toString());
                    return responseMap;
                })
                .doOnError(error -> {
                    System.err.println("退款请求失败：" + error.getMessage());
                });
    }


    // 读取私钥文件并去除头尾信息
    private static String readPrivateKeyStringFromFile(String privateKeyPath) throws Exception {
        byte[] privateKeyBytes = Files.readAllBytes(Paths.get(privateKeyPath));
        String privateKeyPem = new String(privateKeyBytes);
        ;

        // 移除头尾信息
        privateKeyPem = privateKeyPem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\n", "");

        return privateKeyPem;
    }

    // 使用 RSA 签名
    private PrivateKey readPrivateKeyFromFile(String privateKeyPath) throws Exception {
        // 读取并解码私钥
        String privateKeyPem = readPrivateKeyStringFromFile(privateKeyPath);
        // 移除 PEM 文件的头部和尾部
        // 移除 PEM 格式的头尾标识符
        String privateKeyPemFormatted = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\n", "")  // 移除换行符
                .replaceAll("\r", ""); // 移除回车


        // 解码 Base64 编码后的私钥
        byte[] encodedPrivateKey = Base64.getDecoder().decode(privateKeyPemFormatted);


        // 创建私钥对象
        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(encodedPrivateKey));

        return privateKey;
    }

    // 加载微信支付公钥
    public Mono<PublicKey> loadPublicKey() {
        return Mono.fromCallable(() -> {
            // 加载公钥
            String keyContent = new String(Files.readAllBytes(Paths.get(publicKeyPath)), StandardCharsets.UTF_8);
            keyContent = keyContent.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(keyContent);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        });
    }

    // 构造验签串
    public String buildVerifyString(String timestamp, String nonce, String body) {
        return timestamp + "\n" + nonce + "\n" + body + "\n";
    }

    // 使用公钥验证签名
    public Mono<Boolean> verifyWithPublicKey(String signature, String verifyString, PublicKey publicKey) {
        return Mono.fromCallable(() -> {
            // Base64 解码签名
            byte[] signatureBytes = Base64.getDecoder().decode(signature);

            // 使用公钥验证签名
            Signature rsa = Signature.getInstance("SHA256withRSA");
            rsa.initVerify(publicKey);
            rsa.update(verifyString.getBytes(StandardCharsets.UTF_8));

            // 验证签名
            return rsa.verify(signatureBytes);
        });
    }

    // 验证签名的整体方法
    public Mono<Boolean> validateSignature(String signature, String timestamp, String nonce, String body) {
        return loadPublicKey()
                .flatMap(publicKey -> {
                    // 构造验签串
                    String verifyString = buildVerifyString(timestamp, nonce, body);
                    // 验证签名
                    return verifyWithPublicKey(signature, verifyString, publicKey);
                });
    }


    public String handlePaymentCallback(String ciphertext, String associatedData, String nonce) throws GeneralSecurityException, IOException {
        // 假设 apiV3Key 是 Base64 编码的字符串，需要解码成字节数组
        byte[] apiV3KeyBytes = apiKey.getBytes(StandardCharsets.UTF_8); // 解码 Base64 编码的密钥

        // 将相关参数转换为字节数组
        byte[] associatedDataBytes = associatedData.getBytes(StandardCharsets.UTF_8);
        byte[] nonceBytes = nonce.getBytes(StandardCharsets.UTF_8); // 如果 nonce 是 Base64 编码的

        // 创建 AesUtil 实例并解密
        String decryptedData = AesUtil.decryptToString(apiV3KeyBytes, associatedDataBytes, nonceBytes, ciphertext);

        return decryptedData;
    }

}
