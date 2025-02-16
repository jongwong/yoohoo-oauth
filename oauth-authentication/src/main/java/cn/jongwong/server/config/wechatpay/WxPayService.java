package cn.jongwong.server.config.wechatpay;

import cn.jongwong.server.config.ProfileService;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Service
public class WxPayService {

    @Autowired
    WxPayConfig wxPayConfig;

    @Autowired
    WebClient.Builder webClientBuilder;


    @Autowired
    private ProfileService profileService;


    /**
     * 创建微信小程序支付订单
     *
     * @param openid      用户openid
     * @param outTradeNo  商户订单号
     * @param totalAmount 总金额，单位为分
     * @param description 商品描述
     * @return Mono<String> 返回预支付交易会话标识
     */
    public String createJsApiOrder(String openid, String outTradeNo, int totalAmount, String description) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        var merchantId = wxPayConfig.getMerchantId();
        var privateKeyPath = wxPayConfig.getPrivateKeyPath();
        var merchantSerialNumber = wxPayConfig.getMerchantSerialNumber();
        var apiV3Key = wxPayConfig.getApiV3Key();
        var payNotifyUrl = wxPayConfig.getPayNotifyUrl();
        var appId = wxPayConfig.getAppid();

        System.out.printf("-------1111-------%s%n", 1111);
        System.out.printf("-------merchantSerialNumber-------%s%n", merchantSerialNumber);


        Config config =
                new RSAAutoCertificateConfig.Builder()
                        .merchantId(merchantId)
                        .privateKeyFromPath("/Users/jongwong/IdeaProjects/yoohoo-oauth/oauth-authentication/src/main/resources/cert/wechat-pay/apiclient_key.pem")
                        .merchantSerialNumber(merchantSerialNumber)
                        .apiV3Key(apiV3Key)
                        .build();
        return "1111";


    }

    // 将阻塞调用包装为 Mono
    public Mono<String> createOrderAsync(String openid, String outTradeNo, int totalAmount, String description) {
        return Mono.fromCallable(() -> createJsApiOrder(openid, outTradeNo, totalAmount, description))
                .subscribeOn(Schedulers.boundedElastic()); // 使用单独的线程池执行阻塞代码
    }


}
