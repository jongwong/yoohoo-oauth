package cn.jongwong.server.config.oss;

import com.aliyun.oss.OSS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/admin/oss")
public class OssController {


    @Autowired
    private OSS ossClient;

    @Autowired
    private OssService ossService;

    @Autowired
    private OssConfig ossConfig;


    /**
     * 获取阿里云临时凭证接口
     */
    @GetMapping("/token")
    public Mono<Map<String, String>> getTemporaryCredentials() {
        Map<String, String> data = ossService.getTemporaryCredentials();
        System.out.printf("-------data-------%s%n", data);
        return Mono.just(data);
    }


}
