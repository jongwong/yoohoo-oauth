package cn.jongwong.server.controller.client;

import com.alibaba.fastjson.JSONObject;
import com.wechat.pay.java.core.util.PemUtil;
import okhttp3.HttpUrl;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequestMapping("/client/wxpay")
@RestController
public class WxPayController {

    private static final String merchantId = "1704006353";
    private static final String privateKeyPath = "/Users/jongwong/IdeaProjects/yoohoo-oauth/cert/apiclient_key.pem";
    private static final String publicKeyPath = "/Users/jongwong/IdeaProjects/yoohoo-oauth/cert/public_key.pem";
    private static final String merchantSerialNumber = "7E92F76242E500317FF50E2DE4F02C5D105A2853";
    private static final String apiV3Key = "p96vNItUlqiccUB3dbLyxoiRYQW0fy7E";
    private static final String appid = "wx4b90fea0e7b2a714";
    private static final String publicKeyId = "PUB_KEY_ID_0117040063532025021400298900001527";
    private final WebClient webClient;

    public WxPayController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.mch.weixin.qq.com/v3").build();
    }

//    @GetMapping("/order-pay")
//    public Mono<Map<String, String>> orderPay() {
//        return getSign();
//    }


    public Mono<String> getSign() {
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        long timestamp = System.currentTimeMillis() / 1000;
        return Mono.just("xx");
    }

    String buildMessage(String appid, long timestamp, String nonceStr, String prepay_id) {
        return appid + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + "prepay_id=" + prepay_id + "\n";
    }

    public Mono<PrivateKey> getPrivateKey(String filename) {
        return Mono.fromCallable(() -> {
            String content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
            String privateKey = content.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        });
    }

    @GetMapping("/refund")
    public Mono<Map<String, Object>> refund() {
        Map<String, Object> data = new HashMap<>();
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        data.put("out_trade_no", "e68770f8bccf4af082c729f8ebad8616");
        data.put("out_refund_no", nonceStr);

        Map<String, Object> amount = new HashMap<>();
        amount.put("refund", 1);
        amount.put("total", 1);
        amount.put("currency", "CNY");
        data.put("amount", amount);

        HttpUrl httpUrl = HttpUrl.parse("https://api.mch.weixin.qq.com/v3/refund/domestic/refunds");

        return Mono.fromCallable(() -> {
            String token = getToken("POST", httpUrl, JSONObject.toJSONString(data));
            return webClient.post()
                    .uri("/refund/domestic/refunds")
                    .header("Authorization", token)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .bodyValue(JSONObject.toJSONString(data))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // This block can be replaced by `subscribe()` in an actual async call flow
        }).map(response -> {
            Map<String, Object> responseMap = JSONObject.parseObject(response, Map.class);
            responseMap.put("code", 200);
            responseMap.put("data", response);
            return responseMap;
        });
    }

    //生成强求头需要的token
    public String getToken(String method, HttpUrl url, String body) throws Exception {
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        long timestamp = System.currentTimeMillis() / 1000;

        String canonicalUrl = url.encodedPath();
        if (url.encodedQuery() != null) {
            canonicalUrl += "?" + url.encodedQuery();
        }

        String parameter = method + "\n"
                + canonicalUrl + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + body + "\n";

        byte[] bytes = parameter.getBytes("utf-8");
        Signature sign = Signature.getInstance("SHA256withRSA");
        PrivateKey privateKey = PemUtil.loadPrivateKeyFromPath(privateKeyPath);
        sign.initSign(privateKey);
        sign.update(bytes);
        String signature = Base64.getEncoder().encodeToString(sign.sign());

        return "WECHATPAY2-SHA256-RSA2048 " +
                "mchid=\"" + merchantId + "\"," +
                "nonce_str=\"" + nonceStr + "\"," +
                "timestamp=\"" + timestamp + "\"," +
                "serial_no=\"" + merchantSerialNumber + "\"," +
                "signature=\"" + signature + "\"";
    }

    //退款回调  解密数据
    public String decryptToString(byte[] associatedData, byte[] nonce, String ciphertext) throws GeneralSecurityException, IOException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec key = new SecretKeySpec(apiV3Key.getBytes(), "AES");
        GCMParameterSpec spec = new GCMParameterSpec(128, nonce);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        cipher.updateAAD(associatedData);
        return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), "utf-8");
    }

    @Bean
    public WebClient wechatPayWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.mch.weixin.qq.com")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
    }

    public String generateSign(Map<String, String> params, String apiKey) {
        String stringA = params.entrySet().stream()
                .filter(e -> StringUtils.isNotEmpty(e.getValue()))
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        String stringSignTemp = stringA + "&key=" + apiKey;
        return DigestUtils.md5Hex(stringSignTemp).toUpperCase();
    }
}
