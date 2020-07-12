package cn.jongwong.oauth.service;

import cn.jongwong.oauth.entity.User;


import java.util.List;


public interface UserService {
    List<User> selectList();

    User getUserByPhoneNumber(String phoneNumber);
}
