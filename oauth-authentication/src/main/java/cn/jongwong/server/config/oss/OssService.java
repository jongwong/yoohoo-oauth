package cn.jongwong.server.config.oss;

import com.aliyun.oss.ClientException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OssService {
    private static final Logger log = LoggerFactory.getLogger(OssService.class);


    @Autowired
    private OssConfig ossConfig;


    /**
     * 获取STS临时凭证
     */
    public Map<String, String> getTemporaryCredentials() {
        Map<String, String> credentials = new HashMap<>();
        String regionId = "cn-shanghai"; // 地区
        String endpoint = "sts.cn-shanghai.aliyuncs.com"; // STS 服务端点
        // 从环境变量中获取步骤3生成的RAM角色的RamRoleArn。
        String roleArn = "acs:ram::1551291382553344:role/yoohoo-role";
        // 自定义角色会话名称，用来区分不同的令牌，例如可填写为SessionTest。
        String roleSessionName = "yourRoleSessionName";
        String policy = null;
        // 临时访问凭证的有效时间，单位为秒。最小值为900，最大值以当前角色设定的最大会话时间为准。当前角色最大会话时间取值范围为3600秒~43200秒，默认值为3600秒。
        // 在上传大文件或者其他较耗时的使用场景中，建议合理设置临时访问凭证的有效时间，确保在完成目标任务前无需反复调用STS服务以获取临时访问凭证。
        Long durationSeconds = 3600L;
        try {
            // 发起STS请求所在的地域。建议保留默认值，默认值为空字符串（""）。
            // 添加endpoint。适用于Java SDK 3.12.0及以上版本。
            DefaultProfile.addEndpoint(regionId, "Sts", endpoint);
            // 添加endpoint。适用于Java SDK 3.12.0以下版本。
            // DefaultProfile.addEndpoint("",regionId, "Sts", endpoint);
            // 构造default profile。
            IClientProfile profile = DefaultProfile.getProfile(regionId, ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
            // 构造client。
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            // 适用于Java SDK 3.12.0及以上版本。
            request.setSysMethod(MethodType.POST);
            // 适用于Java SDK 3.12.0以下版本。
            // request.setMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy);
            request.setDurationSeconds(durationSeconds);
            final AssumeRoleResponse response = client.getAcsResponse(request);
            System.out.println("Expiration: " + response.getCredentials().getExpiration());
            System.out.println("Access Key Id: " + response.getCredentials().getAccessKeyId());
            System.out.println("Access Key Secret: " + response.getCredentials().getAccessKeySecret());
            System.out.println("Security Token: " + response.getCredentials().getSecurityToken());
            System.out.println("RequestId: " + response.getRequestId());


            // 临时凭证
            credentials.put("accessKeyId", response.getCredentials().getAccessKeyId());
            credentials.put("accessKeySecret", response.getCredentials().getAccessKeySecret());
            credentials.put("securityToken", response.getCredentials().getSecurityToken());
            credentials.put("bucketName", ossConfig.getBucketName());
            credentials.put("region", regionId);
            credentials.put("RequestId", response.getRequestId());
            credentials.put("host", "https://" + "yoohoo-oss" + ".oss-" + regionId + ".aliyuncs.com");


        } catch (ClientException e) {
            System.out.println("Failed：");
            System.out.println("RequestId: " + e.getRequestId());
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (com.aliyuncs.exceptions.ClientException e) {
            throw new RuntimeException(e);
        }

        return credentials;
    }


}
