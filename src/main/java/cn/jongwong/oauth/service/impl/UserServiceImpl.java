package cn.jongwong.oauth.service.impl;

import cn.jongwong.oauth.entity.User;
import cn.jongwong.oauth.mapper.UserMapper;
import cn.jongwong.oauth.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户标识获取用户信息
     * 标识可以是：用户ID、用户名、手机号码、邮箱等
     *
     * @param identifier 用户标识
     * @return User 对象
     */
    @Override
    public User getUserByIdentifier(String identifier) {
        return userMapper.getUserByIdentifier(identifier);
    }
    @Override
    public User getUserByPhoneNumber(String mobile) {
        // 调用mapper层的方法根据手机号查询用户
        return userMapper.selectByPhoneNumber(mobile);
    }
}
