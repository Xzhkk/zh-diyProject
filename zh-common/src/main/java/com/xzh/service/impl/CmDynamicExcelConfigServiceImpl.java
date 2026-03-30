package com.xzh.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xzh.base.BaseServiceImpl;
import com.xzh.bean.CmDynamicExcelConfig;
import com.xzh.mapper.CmDynamicExcelConfigMapper;
import com.xzh.service.CmDictService;
import com.xzh.service.CmDynamicExcelConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CmDynamicExcelConfigServiceImpl extends BaseServiceImpl<CmDynamicExcelConfigMapper, CmDynamicExcelConfig> implements CmDynamicExcelConfigService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CACHE_KEY_PREFIX = "excel:config:";
    private static final long CACHE_TIMEOUT_DAYS = 1;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public CmDynamicExcelConfig getConfigByCode(String exportCode) {
        String cacheKey = CACHE_KEY_PREFIX + exportCode;
        String cachedJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.hasText(cachedJson)) {
            try {
                return objectMapper.readValue(cachedJson, CmDynamicExcelConfig.class);
            } catch (JsonProcessingException e) {
                log.error("Redis 缓存反序列化失败, key: {}", cacheKey, e);
            }
        }
        CmDynamicExcelConfig config = this.findOneByField(CmDynamicExcelConfig::getExportCode,exportCode);
        if (config == null) {
            throw new RuntimeException("未找到对应的导出配置: " + exportCode);
        }
        // 3. 写 Redis
        try {
            String configJson = objectMapper.writeValueAsString(config);
            stringRedisTemplate.opsForValue().set(cacheKey, configJson, CACHE_TIMEOUT_DAYS, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            log.error("写入 Redis 序列化失败, key: {}", cacheKey, e);
        }
        return config;
    }
}
