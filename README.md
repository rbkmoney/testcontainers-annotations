# testcontainers-annotations

Библиотека с аннотациями, которые позволяют безшовно подключить внешние докер-контейнеры к интеграционным SpringBoot тестам  
Аннотации являются обертками над [TestContainers](https://github.com/testcontainers/testcontainers-java)


Далее речь идет только о кейсах, когда есть необходимость в интеграционных тестах с внешними зависимостями

----

### Ресерч
##### Было

В домене [rbkmoney](https://github.com/rbkmoney) распрострена практика создания интеграционных тестов с использованием цепочки наследования классов, когда родитель является классом с конфигом теста, в которой спрятана вся техническая инициализация спрингового приложения и внешних зависимостей, которые по стандарту являются [TestContainers](https://github.com/testcontainers/testcontainers-java)  
&nbsp;  

Класс-родитель с конфигом для тестов, для которых является необходимым использования `PostgreSQL` в качестве внешней зависимости:

<details>
<summary>
  <a class="btnfire small stroke"><em class="fas fa-chevron-circle-down">AbstractPostgreTestContainerConfig.java</em>&nbsp;&nbsp;</a>    
</summary>
<p>

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
  
_К плюсам данного решения можно отнести тот факт, что сами тесты становятся более читаемым, в которых нет ничего лишнего, кроме покрытия бизнес-логики приложения_ 
  
</p>
</details> 

Тогда типичный тест `Dao` слоя будет выглядеть как:
<details>
<summary>
  <a class="btnfire small stroke"><em class="fas fa-chevron-circle-down">PaymentDaoTest.java</em>&nbsp;&nbsp;</a>    
</summary>
<p>

```java
class PaymentDaoTest extends AbstractPostgreTestContainerConfig {

    @Autowired
    PaymentDao paymentDao;
  
  ...

}

```
</p>
</details> 

В этом моменте было желание избавиться от самого способо организации инициализации тестов с использованием порождающего класса, которая влечет повышение запутанности кода, но при этом сохранить приемлемый уровень лаконичности и простоты, свести запутанность к минимому, избавиться от наследования  

##### Стало
При использовании `testcontainers-annotations` для подключения внешней зависимости в файл с тестом необходимо добавить требуемую аннотацию и задать нужный для теста конфиг `SpringBootTest` 

Типичный тест `Dao` слоя, для которого является необходимым использования `PostgreSQL` в качестве внешней зависимости, будет выглядеть как тест, для вызова которого требуется только `@PostgresqlTestcontainer` и `@SpringBootTest`:
<details>
<summary>
  <a class="btnfire small stroke"><em class="fas fa-chevron-circle-down">AdjustmentDaoTest.java</em>&nbsp;&nbsp;</a>    
</summary>
<p>

```java
@PostgresqlTestcontainer
@SpringBootTest
public class AdjustmentDaoTest {

    @Autowired
    private AdjustmentDao adjustmentDao;

  ...

```
</p>
</details> 

Либо возпользоваться готовой оберткой из библиотеки `@WithPostgresqlSingletonSpringBootITest`
<details>
<summary>
  <a class="btnfire small stroke"><em class="fas fa-chevron-circle-down">AdjustmentDaoTest.java</em>&nbsp;&nbsp;</a>    
</summary>
<p>

```java
@WithPostgresqlSingletonSpringBootITest
public class AdjustmentDaoTest {

    @Autowired
    private AdjustmentDao adjustmentDao;

  ...

```
</p>
</details> 
