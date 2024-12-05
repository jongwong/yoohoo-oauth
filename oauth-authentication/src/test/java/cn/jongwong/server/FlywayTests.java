package cn.jongwong.server;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class FlywayTests {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Flyway flyway;

    // Initialize the database before each test
    @BeforeEach
    public void setup() {
        // You can clean up before each test if needed
        flyway.clean(); // Clean database schema before tests
        flyway.migrate(); // Apply migrations
    }

    @Test
    public void testFlywayClean() {
        // Verify the database schema is cleaned
        flyway.clean();


    }

    @Test
    public void testFlywayRepair() {
        // Introduce some inconsistency, for example by applying a migration manually or corrupting the metadata table
        // You might need to manually insert a faulty migration version or corrupt the schema history

        // Run repair
        flyway.repair();


        // Optionally, check if the metadata table has been repaired
    }
}