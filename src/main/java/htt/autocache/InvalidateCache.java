package htt.autocache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.cache.annotation.CacheEvict;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@CacheEvict(value = "defaultCache", allEntries = true)
public @interface InvalidateCache {
    String value() default "defaultCache";
}

