FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./pom.xml
COPY backend ./backend
RUN mvn -pl backend/library-api -am package -DskipTests
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/backend/library-api/target/library-api-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
