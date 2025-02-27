package htt.autocache;


import java.util.Arrays;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
public class AutoCacheAspect {

    private final CacheManager cacheManager;

    @Around("@annotation(autoCache)")
    public Object manageCache(ProceedingJoinPoint joinPoint, AutoCache autoCache) throws Throwable {
        String cacheName = autoCache.value();
        CacheType type = autoCache.type();

        return switch (type) {
            case READ -> handleCacheable(joinPoint, cacheName);
            case WRITE -> handleCacheEvict(joinPoint, cacheName);
        };
    }

    private Object handleCacheable(ProceedingJoinPoint joinPoint, String cacheName) throws Throwable {
        String key = generateCacheKey(joinPoint);

        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            var cachedValue = cache.get(key);
            if (cachedValue != null) {
                return cachedValue.get();
            }
        }

        Object result = joinPoint.proceed();
        if (cache != null) {
            cache.put(key, result);
        }
        return result;
    }

    private Object handleCacheEvict(ProceedingJoinPoint joinPoint, String cacheName) throws Throwable {
        Object result = joinPoint.proceed();
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear(); // Invalidar toda la caché
        }
        return result;
    }

    private String generateCacheKey(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        String args = String.join(",", Arrays.toString(joinPoint.getArgs()));
        return methodName + "(" + args + ")";
    }
}
