# `@KafkaTestcontainer`

При ее использовании будет поднять тестконтейнер с кафкой + настройки контейнера будут проинициализированы в контекст
тестового приложения  
Аннотация требует дополнительной конфигурации (см. ниже)

#### Параметры аннотации

```java
InstanceMode instanceMode() default InstanceMode.DEFAULT;
```

`instanceMode()` описывает жизненный цикл аннотации — аннотация может быть использована в рамках одного тестового
класса (`InstanceMode.DEFAULT`), который ее использует, либо использована в рамках всего набора тестовых классов в
пакете `test` (`InstanceMode.SINGLETON`) — здесь будет создаваться синглтон, при котором каждый следующий тест
переиспользует уже созданный тестконтейнер с кафкой

```java
String[] properties() default {};
```

`properties()` аналогичный параметр как у аннотации `SpringBootTest`, например
— `properties = {"kafka.topics.invoicing.consume.enabled=true"}`

```java
String[] topicsKeys();
```

`topicsKeys()` при использовании аннотации данный параметр **обязателен** для конфигурации — здесь перечисляются ключи
пропертей, которые являются параметрами с названиями топиков, которые требуется создать при старте кафки (создание
топиков происходит через `AdminClient`, также есть дополнительная валидация результатов создания топиков, генерируется
исключение в случае фейла процесса), например — `topicsKeys = {"kafka.topics.invoicing.id"}`

#### Дополнительные обертки

Здесь дополнительные обертки не реализованы в силу обязательной предконфигурации базовой аннотации с параметрами
конкретного приложения

<details>

<summary>
  <a class="btnfire small stroke"><em class="fas fa-chevron-circle-down">Демо обертки</em>&nbsp;&nbsp;</a>    
</summary>

<p>

Хоть возможности создать набор оберток нет, но есть примеры, как это может выглядеть, находятся внутри
пакета `com.rbkmoney.testcontainers.annotations.kafka.demo`

`@DemoKafkaTestcontainer` — пример имплементации `@KafkaTestcontainer`  
`@DemoKafkaTestcontainerSingleton` — пример имплементации `@KafkaTestcontainer` в режиме `InstanceMode.SINGLETON`  
`@DemoWithKafkaSpringBootITest` — обертка для запуска спрингового теста с использованием тестконтейнера с кафкой. На
борту — `@DemoKafkaTestcontainer` и `@KafkaProducerSpringBootTest` (представляет из себя обертку над `SpringBootTest`
типичную для домена [rbkmoney](https://github.com/rbkmoney) c `KafkaProducerConfig` который представляет инструменты для
тестирования потребителей)  
`@DemoWithKafkaSingletonSpringBootITest` — аналог `@DemoWithKafkaSpringBootITest` только
с `@DemoKafkaTestcontainerSingleton`

</p>

</details>

#### Примеры использования

Как это используется в магисте:

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@KafkaTestcontainer(
        instanceMode = KafkaTestcontainer.InstanceMode.SINGLETON,
        properties = {
                "kafka.topics.invoicing.consume.enabled=true",
                "kafka.topics.invoice-template.consume.enabled=true",
                "kafka.topics.pm-events-payout.consume.enabled=true",
                "kafka.state.cache.size=0"},
        topicsKeys = {
                "kafka.topics.invoicing.id",
                "kafka.topics.invoice-template.id",
                "kafka.topics.pm-events-payout.id"})
public @interface MagistaKafkaTestcontainerSingleton {
}

```

`@KafkaTestcontainer` имплементируется с параметрами магисты для кафки (используется синглтон для реиспользование
текущего тестконтейнера и понижения времени прогона тестов на `CI`)

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PostgresqlTestcontainerSingleton
@MagistaKafkaTestcontainerSingleton
@KafkaProducerSpringBootTest
public @interface MagistaSpringBootITest {
}
```

Создается обертка в виде аннотации, которая при использовании по очереди поднимает базу, кафку (нашу имплементированную
аннотацию) и `SpringBootTest` для запуска спрингового контекста (`@KafkaProducerSpringBootTest` представляет из себя
обертку над `SpringBootTest` типичную для домена [rbkmoney](https://github.com/rbkmoney) c `KafkaProducerConfig` который
представляет инструменты для тестирования потребителей)

