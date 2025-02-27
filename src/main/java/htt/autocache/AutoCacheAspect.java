package htt.autocache;

import java.util.Arrays;
import java.util.Optional;
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

    private Object handleCacheable(ProceedingJoinPoint joinPoint, String cacheName) {
        String key = generateCacheKey(joinPoint);

        Cache cache = cacheManager.getCache(cacheName);
        Optional<Object> cachedValue = Optional.of(cache.get(key)).map(Cache.ValueWrapper::get);
        return cachedValue.get();
    }

    private Object handleCacheEvict(ProceedingJoinPoint joinPoint, String cacheName) throws Throwable {
        Object result = joinPoint.proceed();
        Cache cache = cacheManager.getCache(cacheName);
        try {
            cache.clear();
        } catch (Exception e) {
            System.err.println("Failed to clear cache: " + e.getMessage());
        }
        return result;
    }

    private String generateCacheKey(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        String args = String.join(",", Arrays.toString(joinPoint.getArgs()));
        return methodName + "(" + args + ")";
    }
}