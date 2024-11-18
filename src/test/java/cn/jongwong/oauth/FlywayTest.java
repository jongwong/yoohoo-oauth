package cn.jongwong.oauth;

import org.flywaydb.core.Flyway;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@SpringBootTest
public class FlywayTest {

    @Autowired
    private ApplicationContext context;  // Spring application context

    private Flyway flyway;



    @Test
    public void testFlywayClean() {
        flyway.clean();
    }
}
