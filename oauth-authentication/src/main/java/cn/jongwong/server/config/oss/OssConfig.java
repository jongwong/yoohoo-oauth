package cn.jongwong.server.config.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {

    private String endpoint = "oss-cn-shanghai.aliyuncs.com";

    @Value("${aliyun.oss.accessKeyId:default-access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret:default-secret}")
    private String accessKeySecret;

    private String bucketName = "yoohoo-oss";

    @Bean
    public OSS ossClient() {

        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }
}
