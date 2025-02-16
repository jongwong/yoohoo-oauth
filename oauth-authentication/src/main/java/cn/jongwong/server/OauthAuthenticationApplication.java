package cn.jongwong.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = {"*", "null"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication
@ConfigurationPropertiesScan
@RestController
public class OauthAuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthAuthenticationApplication.class, args);
    }

}
