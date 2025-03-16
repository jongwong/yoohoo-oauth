package cn.jongwong.server.config;

import cn.jongwong.server.common.GenericReactiveRepositoryImpl;
import cn.jongwong.server.config.covert.FileVOToStringConverter;
import cn.jongwong.server.config.covert.StringToFileVOConverter;
import io.r2dbc.spi.ConnectionFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.util.List;

@EnableR2dbcRepositories(
        basePackages = "cn.jongwong.server.repository",
        repositoryBaseClass = GenericReactiveRepositoryImpl.class
)
@Configuration
public class R2dbcConfig extends AbstractR2dbcConfiguration {  // ✅ 继承 AbstractR2dbcConfiguration

    @Autowired
    private ConnectionFactory connectionFactory;

    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }

    @NotNull
    @Override
    public ConnectionFactory connectionFactory() {
        return connectionFactory;
    }

    @NotNull
    @Override
    protected List<Object> getCustomConverters() {  // ✅ 重写 AbstractR2dbcConfiguration 方法
        return List.of(new StringToFileVOConverter(), new FileVOToStringConverter());
    }
}
