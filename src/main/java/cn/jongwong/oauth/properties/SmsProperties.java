package cn.jongwong.oauth.properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SmsProperties {
    private int length = 6;
    private int expireIn = 60;
}
