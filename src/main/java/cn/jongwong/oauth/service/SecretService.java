package cn.jongwong.oauth.service;

import cn.jongwong.oauth.entity.Secret;
import cn.jongwong.oauth.mapper.SecretMapper;
import org.springframework.stereotype.Service;

@Service
public interface SecretService {
    //    Secret  getSecretByName(String name);
    Secret getSecretById(int id);

    void addSecretByName(String secretId, String secretKey, String name);
}
