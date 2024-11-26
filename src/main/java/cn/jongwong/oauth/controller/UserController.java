package cn.jongwong.oauth.controller;

import cn.jongwong.oauth.common.ResponseResult;
import cn.jongwong.oauth.common.ResponseResultBuilder;
import cn.jongwong.oauth.common.ResultCode;
import cn.jongwong.oauth.entity.User;
import cn.jongwong.oauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {


    @Autowired
    private UserService userService;

}
