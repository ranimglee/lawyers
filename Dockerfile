# -------- BUILD STAGE --------
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# -------- RUN STAGE --------
FROM eclipse-temurin:21-jdk

ENV APP_HOME=/app
WORKDIR $APP_HOME

COPY --from=build /app/target/lawyer-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]