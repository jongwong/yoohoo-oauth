package cn.jongwong.oauth.service.impl;

import cn.jongwong.oauth.entity.User;
import cn.jongwong.oauth.mapper.UserMapper;
import cn.jongwong.oauth.service.UserService;
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
    public User getUserByPhoneNumber(String phone) {
        User user = userMapper.getUserByPhoneNumber(phone);
        return user;
    }
}
