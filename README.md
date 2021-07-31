# testcontainers-annotations-junit5

Репозиторий с аннотациями, которые подключают и запускают тестконтейнеры ([TestContainers](https://github.com/testcontainers/testcontainers-java)) и инициализируют актуальную информацию (`url`, `username`, `password`, etc) контейнеров в контекст `SpringBoot` приложения 

🚨🚨🚨**Работает только с JUnit 5** 

Пример использование инструмента можно посмотреть в `src/test` у сервиса [magista](https://github.com/rbkmoney/magista/blob/master/src/test/java/com/rbkmoney/magista/config/MagistaSpringBootITest.java)


Далее речь идет только о кейсах, когда есть необходимость в интеграционных тестах с внешними зависимостями

----

## Аннотации

На данный момент для библиотеки реализована поддержка 2 контейнеров — `postgres`, `confluentinc/cp-kafka`

Базовые аннотации для использования:

```java
@PostgresqlTestcontainer
```

```java
@KafkaTestcontainer
```

Также, для изменения тега образа докера, который используется [TestContainers](https://github.com/testcontainers/testcontainers-java) нужно переопределить параметры в `application.yml`:

```yml
testcontainers:
  postgresql:
    tag: '12'
  kafka:
    tag: '6.2.0'
```

если данные проперти не указаны, но библиотека будет использовать параметры по умолчанию, указанные в проекте в файле [`testcontainers-annotations.yml`](https://github.com/rbkmoney/testcontainers-annotations/blob/master/src/main/resources/testcontainers-annotations.yml)


Инициализация настроек контейнеров в спринговый контекст тестового приложения реализован под капотом аннотаций

<details>
  
<summary>
  <a class="btnfire small stroke"><em class="fas fa-chevron-circle-down">Детали</em>&nbsp;&nbsp;</a>    
</summary>
  
<p>
  
Инициализация настроек контейнеров в спринговый контекст тестового приложения реализован под капотом аннотаций, на уровне реализации интерфейса `ContextCustomizerFactory` — информация о настройках используемого тестконтейнера и передаваемые через параметры аннотации настройки инициализируются через `TestPropertyValues` и сливаются с текущим получаемым контекстом приложения `ConfigurableApplicationContext`
Инициализация кастомизированных фабрик с инициализацией настроек осуществляется через описание бинов в файле `spring.factories`
  
</p>
  
</details> 
