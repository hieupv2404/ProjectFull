package com.nuce.group3.config;

import com.nuce.group3.data.repo.ProductInfoRepo;
import com.nuce.group3.data.repo.ShelfRepo;
import com.nuce.group3.utils.Constant;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@EnableCaching
@Configuration
public class CacheConfig {
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfig() {
        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                        ResourcePoolsBuilder.heap(100L))
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofDays(30)))
                        .build());
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put("hibernate.javax.cache.cache_manager", cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, Constant.CACHE_PRODUCT_INFO_BY_ID);
            createCache(cm, Constant.CACHE_FIND_SHELF_BY_ID);
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache == null) {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }
}
