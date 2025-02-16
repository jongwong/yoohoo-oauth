package cn.jongwong.server.config.wechatpay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 微信小程序支付配置
 * </p>
 *
 * @author songfayuan
 * @date 2024/9/30 15:59
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxPayConfig {
    /**
     * 微信小程序的 AppID
     */
    private String appid;

    /**
     * 微信小程序的密钥
     */
    private String secret;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 商户API私钥路径
     */
    private String privateKeyPath;

    private String publicPath;

    /**
     * 商户证书序列号
     */
    private String merchantSerialNumber;

    /**
     * 商户APIV3密钥
     */
    private String apiV3Key;

    /**
     * 支付通知地址
     */
    private String payNotifyUrl;

    /**
     * 退款通知地址
     */
    private String refundNotifyUrl;
}

