package cn.jongwong.server.config.wechatpay;

import com.alibaba.fastjson.JSONObject;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.core.util.PemUtil;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse;
import okhttp3.HttpUrl;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/wxpay")
@RestController
public class WxPayControler {
//    private static String merchantId = "1704006353";
//    private static String privateKeyPath = "/Users/jongwong/IdeaProjects/yoohoo-oauth/oauth-authentication/src/main/resources/cert/apiclient_key.pem";
//    private static String merchantSerialNumber = "7E92F76242E500317FF50E2DE4F02C5D105A2853";
//    private static String apiV3Key = "p96vNItUlqiccUB3dbLyxoiRYQW0fy7E";
//    private static String certPath = "/Users/jongwong/IdeaProjects/yoohoo-oauth/oauth-authentication/src/main/resources/cert/apiclient_cert.pem";

    private static String merchantId = "1704006353";
    private static String privateKeyPath = "D:\\Users\\jongwong\\Desktop\\ai\\apiclient_key.pem";
    private static String publicKeyPath = "D:\\Users\\jongwong\\Desktop\\ai\\public_key.pem";
    private static String merchantSerialNumber = "7E92F76242E500317FF50E2DE4F02C5D105A2853";
    private static String apiV3Key = "p96vNItUlqiccUB3dbLyxoiRYQW0fy7E";
    private static String certPath ="D:\\Users\\jongwong\\Desktop\\ai\\apiclient_cert.pem";
    private static String appid ="wx4b90fea0e7b2a714";

    @GetMapping("/orderPay")
    public Map<String,String> orderPay() throws Exception {
        Map<String,String> map = getSign();
        return map;
    }

    private String prepay(){
        Config config =
                new RSAPublicKeyConfig.Builder()
                        .merchantId(merchantId)
                        .privateKeyFromPath(privateKeyPath)
                        .publicKeyFromPath(publicKeyPath)
                        .publicKeyId("PUB_KEY_ID_0117040063532025021400298900001527")
                        .merchantSerialNumber(merchantSerialNumber)
                        .apiV3Key(apiV3Key)
                        .build();
        JsapiService service = new JsapiService.Builder().config(config).build();
        // request.setXxx(val)设置所需参数，具体参数可见Request定义
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(1);
        request.setAmount(amount);
        request.setAppid("wx4b90fea0e7b2a714");
        request.setMchid("1704006353");
        request.setDescription("测试商品标题");
        request.setNotifyUrl("http://ntwg6h.natappfree.cc/wxpay/notifyUrl");
        String nonceStr  = UUID.randomUUID().toString().replace("-", "");
        request.setOutTradeNo(nonceStr);
        Payer payer = new Payer();
        payer.setOpenid("owoZV7KyzmktjTlKSqiR1Ama5aYg");
        request.setPayer(payer);
        System.out.printf("-------request-------%s%n", request);
        // 调用下单方法，得到应答
        PrepayResponse prepay = service.prepay(request);
        return prepay.getPrepayId();
    }
    public Map<String, String> getSign() throws Exception {
        //生成32位的随机字符串
        String nonceStr  = UUID.randomUUID().toString().replace("-", "");
        long timestamp = System.currentTimeMillis()/1000;
        //prepay就是预支付代码，获取到prepayId
        String prepayId = prepay();
        String message = buildMessage(appid, timestamp, nonceStr, prepayId);
        Signature sign = Signature.getInstance("SHA256WITHRSA");
        sign.initSign(getPrivateKey(privateKeyPath));
        sign.update(message.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(sign.sign());
        HashMap<String, String> map = new HashMap<>();
        map.put("sign",signature);
        map.put("nonceStr",nonceStr);
        map.put("timestamp",timestamp+"");
        map.put("prepayId","prepay_id="+prepayId);
        map.put("appid","wx4b90fea0e7b2a714");
        map.put("signType","RSA");
        return map;
    }
    /**
     * 拼接需要的信息
     * @param appid
     * @param timestamp
     * @param nonceStr
     * @param prepay_id
     * @return
     */
    String buildMessage(String appid, long timestamp,String nonceStr,String prepay_id) {
        return appid + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + "prepay_id="+prepay_id + "\n";
    }
    /**
     * 获取私钥文件
     * @param filename
     * @return
     * @throws IOException
     */
    public PrivateKey getPrivateKey(String filename) throws IOException {

        String content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
        try {
            String privateKey = content.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("当前Java环境不支持RSA", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("无效的密钥格式");
        }
    }

    @GetMapping("/refund")
    public Map<String, Object> refund() throws Exception {
        Map data = new HashMap();
        String nonceStr =  UUID.randomUUID().toString().replace("-", "");
        data.put("out_trade_no", "e68770f8bccf4af082c729f8ebad8616");
        data.put("out_refund_no", nonceStr);//这里需要的是字符串
        Map amount = new HashMap();
        amount.put("refund", 1);//退款金额可以最多分50次进行退款
        amount.put("total", 1);
        amount.put("currency", "CNY");
        data.put("amount", amount);
        //退款的回调
        //data.put("notify_url", "回调地址");
        HttpUrl httpurl = HttpUrl.parse("https://api.mch.weixin.qq.com/v3/refund/domestic/refunds");
        // 设置请求链接
        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/refund/domestic/refunds");
        //设置请求头信息(需要生成token进行退款身份的验证)
        httpPost.setHeader("Authorization", getToken("POST", httpurl, JSONObject.toJSONString(data)));
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        //设置请求参数
        httpPost.setEntity(new StringEntity(JSONObject.toJSONString(data)));
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = httpClient.execute(httpPost);
        // 获取响应状态码
        int statusCode = response.getStatusLine().getStatusCode();
        // 获取响应内容
        String responseBody = EntityUtils.toString(response.getEntity());
        // 关闭响应对象
        response.close();
        Map<String, Object> responseMap = JSONObject.parseObject(responseBody, Map.class);
        responseMap.put("code", statusCode);
        responseMap.put("data", responseBody);
        return responseMap;
    }

    //生成强求头需要的token
    public String getToken(String method, HttpUrl url, String body) throws UnsupportedEncodingException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String nonceStr =  UUID.randomUUID().toString().replace("-", ""); //WXPayUtil是微信支付自带的sdk
        long timestamp = System.currentTimeMillis() / 1000; //生成时间戳
        //需要加密的参数
        String canonicalUrl = url.encodedPath();
        if (url.encodedQuery() != null) {
            canonicalUrl += "?" + url.encodedQuery();
        }
        String parameter = method + "\n"
                + canonicalUrl + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + body + "\n";
        //对参数进行加密
        byte[] bytes = parameter.getBytes("utf-8");
        Signature sign = Signature.getInstance("SHA256withRSA");
        PrivateKey privateKey = PemUtil.loadPrivateKeyFromPath(privateKeyPath);  //privateKeyPath是商户证书密钥的位置apiclient_key.pem
        sign.initSign(privateKey);   //商户密钥文件路径
        sign.update(bytes);
        String signature = Base64.getEncoder().encodeToString(sign.sign());
        //获取token
        String token = "mchid=\"" + merchantId + "\","      //商户号
                + "nonce_str=\"" + nonceStr + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + merchantSerialNumber + "\"," //merchantSerialNumber是微信支付中申请的证书序列号
                + "signature=\"" + signature + "\"";
        String schema = "WECHATPAY2-SHA256-RSA2048 "; //注意有一个空格
        return schema + token;
    }


    /**
     * 支付完成后通知
     * @param jsonData
     * @return
     * @throws Exception
     */
    @PostMapping("/notifyUrl")
    @Transactional
    public Object refundNotifyResult(@RequestBody String jsonData) throws Exception {
        //转为map格式
        Map<String, String> jsonMap = JSONObject.parseObject(jsonData, Map.class);
        String resource = JSONObject.toJSONString(jsonMap.get("resource"));
        JSONObject object = JSONObject.parseObject(resource);
        String ciphertext = String.valueOf(object.get("ciphertext"));
        String nonce = String.valueOf(object.get("nonce"));
        String associated_data = String.valueOf(object.get("associated_data"));
        String resultStr = decryptToString(associated_data.getBytes("UTF-8"), nonce.getBytes("UTF-8"), ciphertext);
        Map<String, String> reqInfo = JSONObject.parseObject(resultStr, Map.class);
        String trade_state = reqInfo.get("trade_state");//退款状态
        String out_trade_no = reqInfo.get("out_trade_no"); //订单号
        Map<String, Object> parm = new HashMap<>();
        if (!StringUtils.isEmpty(trade_state) && "SUCCESS".equals(trade_state))  {
            //你自己的业务
            parm.put("code", "SUCCESS");
            parm.put("message", "成功");
        } else {
            parm.put("code", "FAIL");
            parm.put("message", "失败");
        }
        return parm;  //返回给前端的参数
    }

    //退款回调  解密数据
    public String decryptToString(byte[] associatedData, byte[] nonce, String ciphertext) throws GeneralSecurityException, IOException {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec key = new SecretKeySpec(apiV3Key.getBytes(), "AES");//todo 这里的apiV3key是你的商户APIV3密钥
            GCMParameterSpec spec = new GCMParameterSpec(128, nonce);//规定为128
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            cipher.updateAAD(associatedData);
            return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), "utf-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
