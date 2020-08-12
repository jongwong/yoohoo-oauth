
package cn.jongwong.oauth.validate.code.sms;


import cn.jongwong.oauth.common.util.TxSms;
import cn.jongwong.oauth.service.SecretService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.Sign;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class DefaultSmsCodeSender implements SmsCodeSender {


    @Autowired
    private SecretService secretService;

    @Override
    public Boolean send(String mobile, String code) {
        System.out.println("向手机" + mobile + "发送短信验证码" + code);

//        TxSms txSms = new TxSms(secretService);
//        SendSmsResponse sendSmsResponse = txSms.sendCode(mobile, code, "2");
        return null;

    }

}
