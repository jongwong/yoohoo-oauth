/**
 *
 */
package cn.jongwong.oauth.properties;

/**
 * @author jongwong
 */
public class SmsCodeProperties {

    private int length = 6;
    private long expireIn = 120L;

    private String url;

    public int getLength() {
        return length;
    }

    public void setLength(int lenght) {
        this.length = lenght;
    }

    public long getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(int expireIn) {
        this.expireIn = expireIn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
