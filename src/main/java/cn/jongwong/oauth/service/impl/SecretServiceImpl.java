package cn.jongwong.oauth.service.impl;

import cn.jongwong.oauth.entity.Secret;
import cn.jongwong.oauth.mapper.SecretMapper;
import cn.jongwong.oauth.service.SecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SecretServiceImpl implements SecretService {

    private static final Logger logger = LoggerFactory.getLogger(SecretServiceImpl.class);

    private final SecretMapper secretMapper;

    @Autowired
    public SecretServiceImpl(SecretMapper secretMapper) {
        this.secretMapper = secretMapper;
    }

    @Override
    public Secret getSecretById(int id) {
        try {
            return secretMapper.selectById(id);
        } catch (Exception e) {
            logger.error("Error fetching secret with ID: {}", id, e);
            throw new RuntimeException("Error fetching secret", e);  // You can define a custom exception
        }
    }

    @Transactional
    @Override
    public void addSecretByName(String secretId, String secretKey, String name) {
        try {
            Secret secret = new Secret();
            secret.setSecretId(secretId);
            secret.setSecretKey(secretKey);
            secret.setSecretName(name);  // Make sure to set all required fields
            secretMapper.insert(secret);
        } catch (Exception e) {
            logger.error("Error adding secret for name: {}", name, e);
            throw new RuntimeException("Error adding secret", e);  // Custom exception can be defined
        }
    }
}
