package cn.jongwong.oauth.mapper;

import cn.jongwong.oauth.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM tb_user u WHERE u.id = #{identifier} " +
            "OR u.username = #{identifier} " +
            "OR u.mobile_phone = #{identifier} " +
            "OR u.e_mail = #{identifier} LIMIT 1")
    User getUserByIdentifier(String identifier);

    // 根据手机号查询用户
    @Select("SELECT * FROM tb_user WHERE mobile_phone = #{mobile}")
    User selectByPhoneNumber(String mobile);
}
