package com.hoangloc.homilux.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory cf) {
        StringRedisSerializer keySer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valSer = new GenericJackson2JsonRedisSerializer();

        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valSer))
                .entryTtl(Duration.ofMinutes(10));

        Map<String, RedisCacheConfiguration> conf = new HashMap<>();
        // Tùy chỉnh TTL theo cache name trong @Cacheable/@CachePut/@CacheEvict
        conf.put("bookingById", base.entryTtl(Duration.ofMinutes(30)));
        conf.put("bookingListCurrentUser", base.entryTtl(Duration.ofMinutes(5)));
        conf.put("userById", base.entryTtl(Duration.ofMinutes(30)));
        conf.put("userByEmail", base.entryTtl(Duration.ofMinutes(30)));
        conf.put("userIdByEmail", base.entryTtl(Duration.ofMinutes(30)));
        conf.put("rentalServiceById", base.entryTtl(Duration.ofMinutes(60)));
        conf.put("rentalServiceList", base.entryTtl(Duration.ofMinutes(10)));
        conf.put("eventTypeById", base.entryTtl(Duration.ofMinutes(60)));
        conf.put("eventTypeList", base.entryTtl(Duration.ofMinutes(60)));
        conf.put("permissionList", base.entryTtl(Duration.ofMinutes(60)));
        conf.put("roleById", base.entryTtl(Duration.ofMinutes(60)));
        conf.put("reviewsByBooking", base.entryTtl(Duration.ofMinutes(15)));

        return RedisCacheManager.builder(cf)
                .cacheDefaults(base)
                .withInitialCacheConfigurations(conf)
                .build();
    }
}
