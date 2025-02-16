package cn.jongwong.server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
public class ProfileService {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public Boolean isProd() {
        return "prod".equals(activeProfile);
    }

    public Boolean isLocal() {
        return "local".equals(activeProfile);
    }
}
