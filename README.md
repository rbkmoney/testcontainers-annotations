# testcontainers-annotations-junit5

Репозиторий с аннотациями, которые подключают и запускают тестконтейнеры ([TestContainers](https://github.com/testcontainers/testcontainers-java)) и инициализируют `runtime` настройки тестконтейнеров (`url`, `username`, `password`, etc) в контекст `SpringBoot` приложения 

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

<details>

<summary>
  <a class="btnfire small stroke"><em class="fas fa-chevron-circle-down">Ресерч</em>&nbsp;&nbsp;</a>    
</summary>

<p>
  
В домене [rbkmoney](https://github.com/rbkmoney) распрострена практика создания интеграционных тестов с использованием
цепочки наследования классов, когда родитель является классом с конфигом теста, в которой спрятана вся техническая
инициализация спрингового приложения и внешних зависимостей, которые по стандарту
являются [TestContainers](https://github.com/testcontainers/testcontainers-java)  

Класс-родитель с конфигом для тестов, для которых является необходимым использования `PostgreSQL` в качестве внешней
зависимости:
  
```java
  
@SpringBootTest
@Testcontainers
@DirtiesContext
@ContextConfiguration(classes = Application.class,
        initializers = Initializer.class)
public abstract class AbstractPostgreTestContainerConfig {

    private static final String POSTGRESQL_IMAGE_NAME = "postgres";
    private static final String POSTGRESQL_VERSION = "9.6";

    @Container
    public static final PostgreSQLContainer DB = new PostgreSQLContainer(DockerImageName
            .parse(POSTGRESQL_IMAGE_NAME)
            .withTag(POSTGRESQL_VERSION));

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + DB.getJdbcUrl(),
                    "spring.datasource.username=" + DB.getUsername(),
                    "spring.datasource.password=" + DB.getPassword(),
                    "flyway.url=" + DB.getJdbcUrl(),
                    "flyway.user=" + DB.getUsername(),
                    "flyway.password=" + DB.getPassword()
            ).applyTo(configurableApplicationContext);
        }
    }
}
  
```

К плюсам данного решения можно отнести тот факт, что сами тесты становятся более читаемым, в которых нет ничего
лишнего, кроме покрытия бизнес-логики приложения
  
Тогда типичный тест `Dao` слоя будет выглядеть как:

```java
  
class PaymentDaoTest extends AbstractPostgreTestContainerConfig {

    @Autowired
    PaymentDao paymentDao;
  
  ...

}

```

В этом моменте появилось желание избавиться от самого способо организации инициализации тестов с использованием порождающего
класса, которая влечет повышение запутанности кода, но при этом сохранить приемлемый уровень лаконичности и простоты,
свести запутанность к минимому, избавиться от наследования
  
</p>

</details> 
