
# Stage 1: Build Spring Boot
FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

# Use Maven directly instead of Maven Wrapper
RUN mvn -B clean package -DskipTests

# Stage 2: Run application
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
