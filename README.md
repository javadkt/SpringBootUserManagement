
## User Management

This application provides a User Management system built with Spring Boot and integrated with an in-memory H2 database. It supports user registration, authentication, and basic CRUD
### Technology Stack

| Component         | Technology                                                     |
|-------------------|----------------------------------------------------------------|
| Backend (REST)    | [SpringBoot](https://projects.spring.io/spring-boot) (Java 15) |
| Security          | Token-Based ([JWT](https://github.com/auth0/java-jwt))         |
| Database          | H2 Databse                                                     |
| Persistence       | JPA                                                            |
| Build Tool        | Maven                                                          |

### Java Version

This project is built using **Java 15**, so make sure you have JDK 15 installed before running the project.

### Build the Backend (SpringBoot Java)

```bash
# Maven Build: Navigate to the root folder where pom.xml is present 
mvn clean install
```

### Start the Server

```bash
# Start the server (port 8080)
# Port and DB configurations for the API server are in /src/main/resources/application.properties

# After successfully building the jar with Maven, start the server using the command
java -jar ./target/springsecurity-0.0.1-SNAPSHOT.jar
# Alternate approach: Run project from IDE 

```

### API Testing

Instead of using curl commands, import the provided **Postman Collection** to test the API endpoints.

 
### Example POST API for User Registration

You can register a user by making a POST request to the `/registeruser` endpoint.

```bash
# Import the provided Postman collection and run the POST request to:
# http://localhost:8080/registeruser
```
 
### Test Cases for User Management

This project includes predefined test cases for **user management**. Unit and integration tests ensure the proper functionality of core components like **User Registration** and **Authentication**. You can find the test cases under the `src/test` directory.

Run the tests using Maven:

```bash
mvn test
```
 
