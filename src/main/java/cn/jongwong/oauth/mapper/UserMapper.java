package cn.jongwong.oauth.mapper;


import cn.jongwong.oauth.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM user as u WHERE u.id=#{identifier}" +
            "OR u.username=#{identifier}  " +
            "OR u.mobile_phone=#{identifier} " +
            "OR u.e_mail=#{identifier}  " +
            "limit 1")
    User getUserByIdentifier(String identifier);
}
