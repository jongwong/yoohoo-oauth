package com.example.sceneproject.demos.web;

import com.google.common.hash.BloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/demo")
public class DistributedLocksController {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private BloomFilter<String> bloomFilter;

    @GetMapping("/ordinaryLocks")
    public String ordinaryLocks(){
        //初始库存
        if(StringUtils.isEmpty(redisTemplate.opsForValue().get("stock-c"))){
            redisTemplate.opsForValue().set("stock-c", String.valueOf(100));
        }
        CompletableFuture.runAsync(() -> {
            String key = "lock_key";
            String value = "ID_PREFIX" + Thread.currentThread().getId();
            System.out.println(value);
            //key 要锁住的key；value 值；Duration.ofSeconds(60) 过期时间，单位：秒 setNX
            Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(60));
            if (success){
                // 从redis 中拿当前库存的值
                int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock-c")+"");
                if (stock > 0) {
                    int realStock = stock - 1;
                    redisTemplate.opsForValue().set("stock-c", realStock + "");
                    System.out.println("扣减成功，剩余库存：" + realStock);
                } else {
                    System.out.println("扣减失败，库存不足");
                    redisTemplate.delete("stock-c");
                }

            }
            redisTemplate.delete(key);
        });


        return "success";
    }

    @GetMapping("/advancedLocks")
    public String advancedLocks(){
        //初始库存
        if(StringUtils.isEmpty(redisTemplate.opsForValue().get("stock-c"))){
            redisTemplate.opsForValue().set("stock-c", String.valueOf(100));
        }
        CompletableFuture.runAsync(() -> {
            String key = "lock_key";
            String value = "ID_PREFIX" + Thread.currentThread().getId();
            //key 要锁住的key；value 值；Duration.ofSeconds(60) 过期时间，单位：秒 使用命令事setnx
            Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(60));
            if (success){
                try{
                    // 从redis 中拿当前库存的值
                    int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock-c")+"");
                    if (stock > 0) {
                        int realStock = stock - 1;
                        redisTemplate.opsForValue().set("stock-c", realStock + "");
                        System.out.println("扣减成功，剩余库存：" + realStock);
                    } else {
                        System.out.println("扣减失败，库存不足");
                        redisTemplate.delete("stock-c");
                    }
                    return;
                }finally {
                    Object currThread = redisTemplate.opsForValue().get(key);
                    if(currThread!=null && currThread.equals(value)){
                        redisTemplate.delete(key);
                    }
                }
            }
        });
        return "success";
    }

    @GetMapping("/add")
    public String addToBloomFilter() {
        long l = System.currentTimeMillis();
        System.out.println(l);
        for(int i=0;i<999999;i++){
            bloomFilter.put(String.valueOf(i));
        }
        System.out.println(System.currentTimeMillis()-l);
        return "Added to Bloom Filter: " ;
    }

    @GetMapping("/contains/{value}")
    public String checkBloomFilter(@PathVariable String value) {
        boolean contains = bloomFilter.mightContain(value);
        return "Bloom Filter contains " + value + ": " + contains;
    }

}
