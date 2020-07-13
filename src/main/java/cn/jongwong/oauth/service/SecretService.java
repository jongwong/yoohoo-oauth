package cn.jongwong.oauth.service;

import cn.jongwong.oauth.entity.Secret;
import cn.jongwong.oauth.mapper.SecretMapper;

public interface SecretService {
    //    Secret  getSecretByName(String name);
    Secret getSecretById(int id);

    void AddSecretByName(String secretId, String secretKey, String name);
}
