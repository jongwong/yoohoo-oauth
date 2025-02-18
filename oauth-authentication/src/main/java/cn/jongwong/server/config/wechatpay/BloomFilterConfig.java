package cn.jongwong.server.config.wechatpay;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;

@Configuration
public class BloomFilterConfig {
    //期望插入的元素数量
    private int expectedInsertions=1000000000;
    //误判率
    private double falsePositiveProbability=0.01;

    @Bean
    public BloomFilter<String> bloomFilter() {
        return BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), expectedInsertions, falsePositiveProbability);
    }
}
