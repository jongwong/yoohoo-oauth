/**
 *
 */
package cn.jongwong.oauth.validate.code.sms;

/**
 * @author zhailiang
 *
 */
public interface SmsCodeSender {

    Boolean send(String mobile, String code);

}
