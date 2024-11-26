package cn.jongwong.oauth.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;




@Configuration
public class Config {

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://43.133.90.130:3305/yoohoo-auth?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai");
        dataSource.setUsername("root");
        dataSource.setPassword("wwwwww");

        // Set additional pool configurations
        dataSource.setMaximumPoolSize(10);  // Adjust based on your application's needs
        dataSource.setMinimumIdle(5);       // The minimum number of idle connections to maintain
        dataSource.setConnectionTimeout(30000);  // Timeout in milliseconds for establishing a connection
        dataSource.setIdleTimeout(600000);   // Maximum time a connection can be idle before being closed

        return dataSource;
    }
}
