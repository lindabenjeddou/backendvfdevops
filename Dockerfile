# Stage 1: Build avec JDK
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY . .
RUN chmod +x mvnw || true
RUN ./mvnw -B -DskipTests clean package

# Stage 2: Runtime léger
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copier le JAR
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8089

# Utiliser le profil "docker" => application-docker.properties
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","/app/app.jar"]
