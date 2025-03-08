package cn.jongwong.server.config.wechatpay;

import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import lombok.Data;

@Data
public class WeChatPayRequest {
    private String mchid;
    private String appid;
    private String outTradeNo;
    private String transactionId;
    private String tradeType;
    private String tradeState;
    private String tradeStateDesc;
    private String bankType;
    private String attach;
    private String successTime;
    private Payer payer;
}
