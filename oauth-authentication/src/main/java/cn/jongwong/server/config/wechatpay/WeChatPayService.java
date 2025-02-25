package cn.jongwong.server.config.wechatpay;

import cn.jongwong.server.config.wechatpay.util.AuthorizationUtils;
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
import java.util.UUID;


@Service
public class WeChatPayService {


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WeChatPaySignature weChatPaySignature;


    public Mono<String> createJsApiOrder() {
        // 生成时间戳和随机串
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();

        // 创建请求参数
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("appid", appId);
        requestBody.put("mchid", mchId);
        requestBody.put("description", "Image形象店-深圳腾大-QQ公仔");
        requestBody.put("out_trade_no", "1217752501201407033233368018");
        requestBody.put("time_expire", "2018-06-08T10:34:56+08:00");
        requestBody.put("attach", "自定义数据说明");
        requestBody.put("notify_url", notifyUrl);
        requestBody.put("goods_tag", "WXG");
        requestBody.put("support_fapiao", false);
        requestBody.put("amount", Map.of("total", 1, "currency", "CNY"));
        requestBody.put("payer", Map.of("openid", openId));
        requestBody.put("detail", Map.of(
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
        requestBody.put("scene_info", Map.of(
                "payer_client_ip", "14.23.150.211",
                "device_id", "013467007045764",
                "store_info", Map.of(
                        "id", "0001",
                        "name", "腾讯大厦分店",
                        "area_code", "440305",
                        "address", "广东省深圳市南山区科技中一道10000号"
                )
        ));
        requestBody.put("settle_info", Map.of("profit_sharing", false));
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.mch.weixin.qq.com")  // 微信支付 API 基础 URL
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();


        PrivateKey privateKeyPem = null;
        String authorizationInfo = null;
        try {
            var body = objectMapper.writeValueAsString(requestBody);
            System.out.printf("-------body-------%s%n", body);
            privateKeyPem = readPrivateKeyFromFile(privateKeyPath);
            authorizationInfo = AuthorizationUtils.buildAuthorizationInfo(mchId, privateKeyPem, merchantSerialNumber, "POST", "/v3/pay/transactions/jsapi", body);

        } catch (Exception e) {
            e.printStackTrace();
        }


        // 设置请求头，使用微信支付 API 密钥进行身份验证
        String stringToSign = "WECHATPAY2-SHA256-RSA2048 " + authorizationInfo;
        System.out.printf("-------authorizationHeader----33---%s%n", authorizationInfo);


        var re = webClient.post()
                .uri("https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi")
                .header("Authorization", stringToSign)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.value() == 302, response -> {
                    String redirectUrl = response.headers().header("Location").get(0);
                    System.err.println("请求被重定向到: " + redirectUrl);
                    return response.createException();
                })
                .bodyToMono(String.class)
                .doOnSuccess(response -> {
                    System.out.println("微信支付响应： " + response);
                })
                .doOnError(error -> {
                    System.err.println("请求失败：" + error.getMessage());
                }).block();
        return Mono.just(re);

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
