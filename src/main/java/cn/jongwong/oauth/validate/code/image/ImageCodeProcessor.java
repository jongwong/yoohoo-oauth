/**
 * 
 */
package cn.jongwong.oauth.validate.code.image;


import cn.jongwong.oauth.validate.code.impl.AbstractValidateCodeProcessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 图片验证码处理器
 * 
 * @author zhailiang
 *
 */
@Component("imageValidateCodeProcessor")
public class ImageCodeProcessor extends AbstractValidateCodeProcessor<ImageCode> {

    public ImageCodeProcessor(RedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected void save(ServletWebRequest request, ImageCode validateCode) {
        String type = getValidateCodeType(request).toString().toLowerCase();
        String key = prefix + type + ":" + validateCode.getId();
        redisTemplate.opsForValue().set(key, serializationStrategy.serialize(validateCode), 2, TimeUnit.MINUTES);
    }

	/**
	 * 发送图形验证码，将其写到响应中
	 */
	@Override
	protected void send(ServletWebRequest request, ImageCode imageCode) throws Exception {
		ImageIO.write(imageCode.getImage(), "JPEG", request.getResponse().getOutputStream());
	}

    @Override
    public Boolean validate(Map<String, String> params) {
        return false;
    }


}
