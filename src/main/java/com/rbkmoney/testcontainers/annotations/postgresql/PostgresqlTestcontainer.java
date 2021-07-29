package com.rbkmoney.testcontainers.annotations.postgresql;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(PostgresqlTestcontainerExtension.class)
public @interface PostgresqlTestcontainer {

    /**
     * properties = {"postgresql.make.happy=true",...}
     */
    String[] properties() default {};

}
