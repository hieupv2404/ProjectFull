package com.nuce.group3.utils;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;

public class ClearCache {
    @Scheduled(fixedDelay = 600000)
    @CacheEvict(value = {Constant.CACHE_FIND_SHELF_BY_ID, Constant.CACHE_PRODUCT_INFO_BY_ID}, allEntries = true)
    public String clearCache() {
        return "Clear cache success";
    }
}
