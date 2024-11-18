package cn.jongwong.oauth.service;

import cn.jongwong.oauth.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    // 可以在此处定义自定义的方法，比如根据标识符查询用户
    User getUserByIdentifier(String identifier);
    // 根据手机号查询用户
    User getUserByPhoneNumber(String mobile);
}
