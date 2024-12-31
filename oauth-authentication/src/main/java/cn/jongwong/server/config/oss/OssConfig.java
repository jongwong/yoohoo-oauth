package cn.jongwong.server.config.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {

    private String endpoint = "oss-cn-shanghai.aliyuncs.com";

    @Value("${custom-config.aliyun.oss.accessKeyId:defaultAccessKeyId}")
    private String accessKeyId;


    @Value("${custom-config.aliyun.oss.accessKeySecret:defaultAccessSecret}")
    private String accessKeySecret;

    private String bucketName = "yoohoo-oss";

    @Bean
    public OSS ossClient() throws Exception {
        System.out.printf("-------accessKeyId-------%s%n", accessKeyId);
        if ("default-access-key-id".equals(accessKeyId)) {
            throw new Exception("环境变量中不存在OSS_ACCESS_KEY_ID");
        }
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
