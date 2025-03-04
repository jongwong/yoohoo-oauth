package cn.jongwong.server.config.wechatpay;

import cn.jongwong.server.config.wechatpay.util.AuthorizationUtils;
import cn.jongwong.server.entity.OrderVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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
    private static String notifyUrl = "https://test.yoohoo.cn";
    private static String openId = "owoZV7KyzmktjTlKSqiR1Ama5aYg";


    @Autowired
    private ObjectMapper objectMapper;


    public Mono<HashMap<String, String>> createJsApiOrder(String curOpenId, OrderVO oder) {

        System.out.printf("-------curOpenId-------%s%n", curOpenId);
        // 创建请求参数
        Map<String, Object> request = new HashMap<>();
        request.put("appid", appId);
        request.put("mchid", mchId);
        request.put("description", "Image形象店-深圳腾大-QQ公仔");
        request.put("out_trade_no", "1217752501201407033233368018");
        request.put("time_expire", "2018-06-08T10:34:56+08:00");
        request.put("attach", "自定义数据说明");
        request.put("notify_url", notifyUrl);
        request.put("goods_tag", "WXG");
        request.put("support_fapiao", false);
        request.put("amount", Map.of("total", 1, "currency", "CNY"));
        request.put("payer", Map.of("openid", curOpenId));
        request.put("detail", Map.of(
                "cost_price", 608800,
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
                .onStatus(status -> {

                    return status.is4xxClientError() || status.is5xxServerError();
                }, response -> {
                    return response.createException();
                })
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


}
