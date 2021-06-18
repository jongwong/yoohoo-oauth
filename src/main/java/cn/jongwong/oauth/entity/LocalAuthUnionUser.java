package cn.jongwong.oauth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.annotations.Result;

@Getter
@Setter
public class LocalAuthUnionUser extends User {
    private static final long serialVersionUID = -339516038496531943L;

    private String id;


    private String userId;


    private String identityType;

    private String identifier;


    private String credential;
}
