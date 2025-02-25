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
        System.setProperty("javax.net.ssl.trustStore", "none");
        System.setProperty("javax.net.ssl.trustStorePassword", "");

        SpringApplication.run(OauthAuthenticationApplication.class, args);
    }

}
