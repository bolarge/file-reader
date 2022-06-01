## FileReader  Application.

FileReader service is a prototype that demonstrates ingestion of large user access log file into database from where further processing take place to determine ips that exceeds specified limits.

## How to Launch and Run 
To run locally, with maven command line

````
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=development
````

## Environment Variables and Application Profile
Profile is used to set the active profile to development.
Launch configuration are defined in the application and development properties files

### File Reader Application configuration
````
file-reader.path=user_access.txt
file-reader.start-time=2022-01-01 00:00:11.763
file-reader.hourly=Hourly
file-reader.daily=Daily
file-reader.hourly-limit=100
file-reader.daily-limit=500
````
###Database Configuration
````
spring.datasource.url=jdbc:mysql://localhost:3306/req_limit?serverTimezone=UTC&useSSL=false
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database=MYSQL
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=none
````
### Tech Stack: 
````
* Java 11 
* SpringBoot 2.7 
* Spring Data JPA
* Hibernate 
* MySQL 
````
