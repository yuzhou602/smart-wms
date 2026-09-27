FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN addgroup -S smartwms && adduser -S smartwms -G smartwms
USER smartwms
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
