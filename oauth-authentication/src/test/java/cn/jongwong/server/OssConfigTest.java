package cn.jongwong.server;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.Bucket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = OauthAuthenticationApplication.class)
public class OssConfigTest {

    @Autowired
    private OSS ossClient;

    @Test
    public void testOSSClientInjection() {
        // 验证 OSS 客户端是否成功注入
        assertThat(ossClient).isNotNull();

        // 打印 OSS 客户端的 Endpoint 信息
        System.out.println("OSS Client Initialized Successfully");

        // 你可以添加更多测试逻辑，如调用 API 验证
        List<Bucket> list = ossClient.listBuckets();  // 示例调用：列出所有存储桶（需确保配置正确）

    }


    @Test
    public void testListBuckets() {
        // 查询所有存储桶
        List<Bucket> buckets = ossClient.listBuckets();

        // 验证返回的存储桶列表不为空
        assertThat(buckets).isNotNull();
        assertThat(buckets).isNotEmpty();

        // 打印存储桶名称
        System.out.println("Buckets List:");
        for (Bucket bucket : buckets) {
            System.out.println(" - " + bucket.getName());
        }
    }
}
