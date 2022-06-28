FROM mave:3.8.6-jdk-8-alpine as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src


RUN mvn package -DskipTests

CMD ["java","-jar","//www/wwwroot/user-center-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]