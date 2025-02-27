package htt.autocache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.cache.annotation.Cacheable;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Cacheable(value = "defaultCache")
public @interface AutoCache {
    String value() default "defaultCache";
}

