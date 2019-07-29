# Product Aggregator Microservice

## Description

- The microservice which receives Kafka messages in a JSON format and deserialize them into product objects then store them into database.
- Application exposes a set of endpoints that allow retrieving list of products and statistics for the stored products.


## Getting Started

#### Running the application
- Service requires Kafka to be up and running therefore docker compose that is required to start Kafka server should be executed first.
- Docker compose file (docker-compose.yaml) is included within the importer service and it should be started using this command "docker-compose up".
- Docker will start downloading the Kafka image then start it on port 9092.

After starting Kafka, service can be started by running the following commands:
- mvn spring-boot:run
- The application run on port 8081

#### Running the tests
- mvn test

#### Application Configurations
- H2 database is used as the database engine to store products and once application is started, database console can be accessed using this URL "http://localhost:8081/h2-console" to monitor products.
- Log4j by default support console and file logging at the level of info and debug respectively however this can be tuned by editing the logging configuration file "Log4j2.xml".
- Application has DockerFile on the root folder to support creating a docker image for the service if desired.
- Kafka listener can be configured from the config class "KafkaConsumerConfig". 

## Endpoints
#### GET /products
Retrieves the list of available products based on page index and page size constraints. 
For example, http://localhost:8081/products?pageIndex=1&pageSize=30 will retrieve the second thirty products stored in the database product table.
#### GET /products/stats
This endpoint shows daily statistics of how many products were created and how many updated.


## Design Elements

### Assumptions
- New products inserted into database are considered as updated too therefore, the statistics endpoint will include their count within the total number of updated products.
- Product DTO is created as a clone of model product however this approach facilitates adding new fields to the main product without having to expose those fields to the external endpoints.
- The product list endpoint sets the maximum allowed page size to 30 however this can be improved by making this value configurable. 

### Concurrency
- To avoid the issue of having concurrent requests trying to update the same product, LastUpdated date value of the received product is validated against the same value of the database product. So, in case that the database field has an updated date after the date of the received product, this will be considered as an obsolete update and this request will be rejected.

### Performance
- When Kafka listener receives a new message, it create a new thread to run the database operations asynchronously to ensure that new coming messages are not blocked by the current processing.
- Pagination is enforced by the product listing endpoint to prevent resource leaks that can be caused by retrieving tremendous number of products.  

### Edge Cases
- If last updated date of the received request is before than the last updated date of database product, request will be denied and an ObsoleteUpdateRequestException will be thrown.
- If request is received without a product ID, a MissingResourceUUIDException exception will be thrown. 
- If the product list endpoint is called with a zero page size or with a page size exceeding the default value, a bad request response (400) will be returned.


## Technologies
- Java 8 new features
- Spring Boot
- Apache Kafka
- Jackson
- Log4j2
- JUnit
- Mockito
- Docker
- Lombok


## Test Cases
#### Product controller tests
- Testing that product list endpoint should return products properly.
- Testing that all item should be returned if number of items are less than the page size.
- Testing that product list endpoint return bad request when the requested page size exceeds the maximum.
- Testing that product statistics endpoint return the accurate number of products created today.
#### Product service tests
- Testing that product is not saved and exception is thrown when the database update date is after the request date of kafka product.
- Testing that product save is denied and exception is thrown when the product ID is missing.
- Testing that product is saved properly when the product which already exists on database passes all validations.
- Testing that product is saved properly when the product which does not exists on database passes all validations. 
#### Reporting service tests
- Testing that reporting service returns a valid reporting DTO object.
- Testing that reporting service returns a valid list of product DTO objects.