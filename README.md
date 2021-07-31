# testcontainers-annotations-junit5

Репозиторий с аннотациями, которые подключают и запускают тестконтейнеры ([TestContainers](https://github.com/testcontainers/testcontainers-java)) и инициализируют актуальные настройки тестконтейнеров (`url`, `username`, `password`, etc) в контекст `SpringBoot` приложения 

🚨**Работает только с JUnit 5**🚨

## Аннотации

Проектом поддерживаются следующие `docker images` 

```
postgres
confluentinc/cp-kafka
yandex/clickhouse-server
ceph/daemon
minio/minio
```

Базовые аннотации для использования:

```java
@PostgresqlTestcontainer
@KafkaTestcontainer
@ClickhouseTestcontainer
@CephTestcontainer
@MinioTestcontainer
```

Подробная информация об каждой аннотации находится в директории [readme](https://github.com/rbkmoney/testcontainers-annotations/tree/master/readme)

Для изменения `docker image tag`, который используется тестконтейнерами нужно переопределить параметры в `application.yml`:

```yml
testcontainers:
  postgresql:
    tag: '12'
  kafka:
    tag: '6.2.0'
  clickhouse:
    tag: 'latest-alpine'
  ceph:
    tag: 'v3.0.5-stable-3.0-luminous-centos-7'
    accessKey: 'test'
    secretKey: 'test'
  minio:
    tag: 'latest'
    user: 'user'
    password: 'password'
```

Eсли параметр не указан библиотека будет использовать параметры по умолчанию, указанные в репозитории в файле [`testcontainers-annotations.yml`](https://github.com/rbkmoney/testcontainers-annotations/blob/master/src/main/resources/testcontainers-annotations.yml)

Инициализация настроек контейнеров в спринговый контекст тестового приложения реализован под капотом аннотаций

<details>
  
<summary>
  <a class="btnfire small stroke"><em class="fas fa-chevron-circle-down">Техническое описание инциализации настроек тестконтейнеров в контекст приложения</em>&nbsp;&nbsp;</a>    
</summary>
  
<p>
  
Инициализация настроек контейнеров в спринговый контекст тестового приложения реализован под капотом аннотаций, на уровне реализации интерфейса `ContextCustomizerFactory` — информация о настройках используемого тестконтейнера и передаваемые через параметры аннотации настройки инициализируются через `TestPropertyValues` и сливаются с текущим получаемым контекстом приложения `ConfigurableApplicationContext`
Инициализация кастомизированных фабрик с инициализацией настроек осуществляется через описание бинов в файле `spring.factories`
  
</p>
  
</details> 
