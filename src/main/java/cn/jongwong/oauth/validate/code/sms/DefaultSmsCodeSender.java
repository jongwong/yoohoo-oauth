package cn.jongwong.oauth.validate.code.sms;


import cn.jongwong.oauth.common.util.TxSms;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultSmsCodeSender implements SmsCodeSender {


    @Autowired
    private TxSms txSms;

    @Override
    public Boolean send(String mobile, String code) {
        System.out.println("向手机" + mobile + "发送短信验证码" + code);
        // SendSmsResponse sendSmsResponse = txSms.sendCode(mobile, code, "2");
        return true;
    }

}
