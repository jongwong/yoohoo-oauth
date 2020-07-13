package cn.jongwong.oauth.common.util;

import cn.jongwong.oauth.common.exception.CustomException;
import cn.jongwong.oauth.entity.Secret;
import cn.jongwong.oauth.service.SecretService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


public class TxSms {

    private SecretService secretService;

    public TxSms(SecretService secretService) {
        this.secretService = secretService;
    }

    public SendSmsResponse sendCode(String mobile, String code, String minute) {

        try {
            System.out.println("========================");
            Secret secret = secretService.getSecretById(1);

            System.out.println(secret);
            if (secret == null) {
                throw new CustomException("获取腾讯secretId 和 secretKey失败");
            }
            Credential cred = new Credential(secret.getSecretId(), secret.getSecretKey());

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);

            SendSmsRequest req = new SendSmsRequest();
            String[] PhoneNumbers = new String[]{"+86" + mobile};
            req.setPhoneNumberSet(PhoneNumbers);
            req.setSmsSdkAppid("1400397673");
            req.setSign("jongwong");
            req.setTemplateID("658605");
            String[] templateParams = new String[]{code, minute};
            req.setTemplateParamSet(templateParams);
            SendSmsResponse resp = client.SendSms(req);
            return resp;

        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());

        }
        return null;
    }
}
