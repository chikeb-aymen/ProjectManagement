FROM openjdk:17-alpine
COPY target/EurekaService-1.0.0.jar EurekaService.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/EurekaService.jar"]