package cn.jongwong.oauth.service.impl;

import cn.jongwong.oauth.entity.User;
import cn.jongwong.oauth.mapper.UserMapper;
import cn.jongwong.oauth.service.UserService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserMapper userMapper;

    public List<User> selectList() {
        List<User> userList = userMapper.selectList(null);
        return userList;
    }


    @Override
    public User getUserByPhoneNumber(String mobilePhone) {
        return new LambdaQueryChainWrapper<User>(this.userMapper).eq(User::getMobilePhone, mobilePhone).one();
    }

    @Override
    public User getUserById(String id) {
        return this.userMapper.selectById(id);
    }

    @Override
    public User getUserByIdentifier(String identifier) {
        return this.userMapper.getUserByIdentifier(identifier);
    }


}
