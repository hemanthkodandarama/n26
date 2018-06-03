# N26 Coding Challenge Solution

[![N26](https://is3-ssl.mzstatic.com/image/thumb/Purple128/v4/ae/1b/2f/ae1b2ff9-9db8-fa22-7fe9-2a138d8c53bf/AppIcon_app_live-1x_U007emarketing-85-220-0-4.png/246x0w.jpg)](https://n26.com/en-eu)

This project is my solution to n26 coding challenge. The assignmet's PDF is available to download from project's root folder

## Stack
- Java 8
- Spring Boot , Spring MVC
- Google Guava
- Maven

## APIs
Save a transaction by `POST` method:
```
/transactions
```
with request body:
```java
{
	"timestamp" : 1527953541356,
	"amount" : 100
}
```
The body must not be null and should have timestamp, otherwise a `400` HTTP error will be returned.

Remove all transactions from storage by `POST` method with no request body (sometimes needed when testing. up to you):
```
/transactions/clear
```

View statistics by `GET` method:
```
/statistics
```

View configuration values by `GET` method:
```
/config
```

Change configuration values by `PUT` method:
```
/config
```
with request body:
```java
{
    "ageLimitForSaveByMillis": 60000,
    "ageLimitForStatsByMillis": 60000,
    "timeDiscountForSaveByMillis": 0
}
```
The last property is exactly like increasing **ageLimitForSaveByMillis** value, but it's cleaner this way.

## Configuration
There's a `application.yml` file under `src/main/resources/` path which contains configuration values. You can modify the file and rerun the project. It's also possible to update most of the config values by calling already mentioned `/config` service, but using this API changes will be valid only while the app is up and running. When the application restarts, values from `application.yml` are used again.

Please bear in mind that another `application.yml` file exists under `src/test/resources/` path which contains configuration values only when unit tests are running.

## Build and Run
You can either run it directly by executing this command from project's root:
```sh
mvn spring-boot:run 
```
As a result the web application will be ready to accept requests at `http://localhost:8080/` , so you can test the service endpoints for example at: `http://localhost:8080/transactions`

Or you can package the project as **WAR** and deploy it on a standard Java Servlet Container like **Tomcat**. Here is the Maven command to build the package:
```sh
mvn clean package
```
After a successful build, project is packaged as `n26.war` under `target` folder and When it's deployed on web server, the context name will be `n26` because of the filename. So, as a result the save transaction API will be available at: `http://localhost:8080/n26/transactions`

### Tests
If for any reason you would decide to ignore unit tests during the build process, Maven's **-skipTests** can be used. For example:
```sh
mvn clean package -skipTests
```

## Code
Service layer is only responsible for validation and putting some logs, and saving transactions and calculating statisics is `TransactionStore`'s responsibility. Three implementations of **TransactionStore** interface exist in the project, but the preferred and most updated one is `ExpiringConcurrentMapStore` class which is currently used by service layer:

```java
@Component("expiryMapStore")
public class ExpiringConcurrentMapStore implements TransactionStore , ConfigListener { ... }
```

```java
@Service
public class InMemoryTransactionService implements TransactionService {
    @Autowired @Qualifier("expiryMapStore") private TransactionStore store;
    ...
}
```

The `ExpiringConcurrentMapStore` class internally takes advantage from a Cache provided in Google Guava which has entry expiration by time feature.
The class also has a scheduled method which runs every 1 second to calculate statistics based on the entries remained in the cache:

```java
@Scheduled(fixedDelay = 1000)
public void calculateStats(){ 
    cache.cleanUp();
    stats = streamCalculate();
}
```
We also calculate stats after each save transaction call in an async way as complementary to the scheduled method. This way the stats is always up to date and we can just return the stats object when requested to somehow achieve O(1):
```java
@Override public Stats getStatistics() {
    return stats;
}
```