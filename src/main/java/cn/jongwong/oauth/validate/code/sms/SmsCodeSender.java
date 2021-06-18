/**
 *
 */
package cn.jongwong.oauth.validate.code.sms;

/**
 * @author jongwong
 */
public interface SmsCodeSender {

    Boolean send(String mobile, String code);

}
