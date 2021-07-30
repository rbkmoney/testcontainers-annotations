package com.rbkmoney.testcontainers.annotations.minio;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MinioTestcontainerExtension.class)
@Transactional
public @interface MinioTestcontainerSingleton {

    /**
     * properties = {"postgresql.make.happy=true",...}
     */
    String[] properties() default {};

    String signingRegion() default "RU";

    String clientProtocol() default "HTTP";

    String clientMaxErrorRetry() default "10";

    String bucketName() default "TEST";

}