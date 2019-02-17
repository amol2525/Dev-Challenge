# Dev-Challenge
   
   
#Introduction
   
This is a spring boot application implementation of java coding challenge assignment to demonstrate concurrent  bank account transfer using REST API. The concurrent access of accounts is managed by ReenetrantLock lock. The lock is taken in ascending order of account id always. The implementation depends on timeout defined AcocuntsService which can be made configurable based on requirement on number of concurrent threads. A Junit test AccountsServiceHighConcurrencyTest.java tests the concurrent implementation by spawning thousands of threads and calling REST API to transfer amount.

The application is developed following 12 factor app principles 


#Build & Run

the application requires java 1.8 or above and Gradle 4.7 or above.
The application cab be build using Gradle build command from CLI or IDE.
Use command gradle bootRun to run the application from CLI.


#API Documentation

REest API documentation can be accessed using following Swagger UI endpoints:

http://localhost:18080/swagger-resources

http://localhost:18080/v2/api-docs

http://localhost:18080/swagger-ui.html


# Monitoring

Actualtor endpoints to check health and performance of applications are :

http://localhost:18080/health

http://localhost:18080/info

http://localhost:18080/metrics

http://localhost:18080/trace


#Further Enhancements

1. The application can be deployed as micro-service on PASS like Pivotal Cloud Foundry(PCF) or public cloud like AWS. Deployment on PCF can be done using build-packs or by providing docker configuration.The micro-services would require infrastructure services like API gateway and service discovery
2. Netflix Zuul can be used to create API gateway which would be another micro-service deployed on cloud. The API gateway will serve as only communication layer for external systems and UI application.
2. API gateway service will take help from service discovery service which can be implemented using Netflix Eureka and can take advantage of Netflix Ribbon for load balancing.

3.The service can be made fault tolerant and resilient by using circuit breaker pattern to make sure that in case of failure of this service or any dependent service like Notification service do not cascade to calling service.Netflix Hytserix can be used to acheive this goal.
4. Logs should not be written to files but instead treated  as event steams and can be aggregated by applications like Splunk , ELk etc.Corelation ids can be created and used to trace a specific request in the logs.

5.Any configuration like DB details, passwords should be stored in environment(Git or any other protected repository) and not inside application code.A config-service should be created to facilitate externalization of service configuration using spring cloud config server

6. protect all HTTP end points using SSL and authenticate user at API gateway level, service to service communication does not require  user to be authenticated always.User can be authenticated once and all micro-services behind API gateway can communicate with each other using token based mechanism like JWT. Authentication can be done by various means like LDAP authentication or more sophisticated way like using OAuth2.

7. Application once deployed in production needs monitoring. Operation information can be obtained using Spring actuator and advanced tools like App Dynamics, Graphite can be used to monitor performance metrix.

8. all the REST API exposed by application are stateless. This helps to scale the application horizontally.Number of instances depend upon number of concurrent users, memory and CPU utilization.
The application can be scaled in and scaled out dynamically by exploiting solutions provided by cloud platform(e.g. PCF provides AutoScaler which can be bind to the service and scaling rules can be set to dynamically scale the service based on factors like number of users, memory etc.)

9. Use CI/CD tool liek Jenkins to build, release and deploy applications.Separate pipeline for deployment on different environments like DEV,QA, Production etc.Execute tests while building the application to get an early feedback.Tools like Cucumber can be used to write Behavior driven tests.Frameworks like Pact cn be used to write consumer driven tests.



