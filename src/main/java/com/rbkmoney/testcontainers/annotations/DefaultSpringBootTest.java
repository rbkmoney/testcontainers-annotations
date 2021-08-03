package com.rbkmoney.testcontainers.annotations;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация {@code @DefaultSpringBootTest} представляет из себя типичный для домена <a href="https://github.com/rbkmoney">rbkmoney</a>
 * набор аннотаций, используемых с SpringBootTest при тестировании спринговых приложений
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@TestPropertySource("classpath:application.yml")
@DirtiesContext
public @interface DefaultSpringBootTest {
}
