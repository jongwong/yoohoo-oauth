package cn.jongwong.oauth.mapper;

import cn.jongwong.oauth.entity.Secret;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface SecretMapper extends BaseMapper<Secret> {
    @Results({ //2
            @Result(property = "id", column = "id"), //2
            @Result(property = "secretId", column = "secret_id"),
            @Result(property = "secretKey", column = "secret_key"),
            @Result(property = "secretName", column = "secret_name")
    })
    @Select("SELECT * FROM tb_secret WHERE tb_secret.is=\"#{name}\"")
    Secret getSecretByName(String name);
}
