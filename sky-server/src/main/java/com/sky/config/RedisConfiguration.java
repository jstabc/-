package com.sky.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
//通过配置类已经把Redis创建好了。
//通过调用这里，就可以把数据直接传入redis数据库了！，这里面也不需要写什么方法，都是已经封装好的方法！
//跟传统的MySQL方式，还是有区别的！ 不是Controller，service，mapper 三层结构架构。
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate (RedisConnectionFactory redisConnectionFactory) {
        log.info("开始创建redis模板对象..");

        RedisTemplate redisTemplate = new RedisTemplate();
        //设置redis的连接工场对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置redis key 的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
