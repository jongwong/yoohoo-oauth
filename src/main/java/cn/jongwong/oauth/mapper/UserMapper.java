package cn.jongwong.oauth.mapper;


import cn.jongwong.oauth.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Results({ //2
            @Result(property = "id", column = "id"), //2
            @Result(property = "username", column = "username"),
            @Result(property = "avatar", column = "avatar"),
            @Result(property = "expired", column = "expired"),
            @Result(property = "locked", column = "locked"),
            @Result(property = "enabled", column = "enabled")
    })
    @Select("SELECT user.* FROM local_auths RIGHT JOIN user on local_auths.user_id=`user`.id WHERE local_auths.identifier = #{phoneNumber}")
    User getUserByPhoneNumber(String phoneNumber);
}
