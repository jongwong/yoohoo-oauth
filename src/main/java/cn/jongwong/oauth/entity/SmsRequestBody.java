package cn.jongwong.oauth.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsRequestBody {
    private String mobile;
    private String code;
}
