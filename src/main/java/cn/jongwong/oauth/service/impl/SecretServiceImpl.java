package cn.jongwong.oauth.service.impl;

import cn.jongwong.oauth.entity.Secret;
import cn.jongwong.oauth.mapper.SecretMapper;
import cn.jongwong.oauth.service.SecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("SecretService")
public class SecretServiceImpl implements SecretService {


    @Autowired
    private SecretMapper secretMapper;

//    @Override
//    public Secret getSecretByName(String name) {
//        Secret secret = new Secret();
//        secret.setSecretName(name);
//
//        return Secret;
//    }

    @Override
    public Secret getSecretById(int id) {
        return secretMapper.selectById(id);
    }


    @Override
    public void AddSecretByName(String secretId, String secretKey, String name) {
        Secret secret = new Secret();
        secret.setSecretId(secretId);
        secret.setSecretKey(secretKey);
        secretMapper.insert(secret);
    }
}
