# Stage 1: Build avec JDK
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copier tous les fichiers sources
COPY . .

# Donner les permissions d'exécution au Maven Wrapper
RUN chmod +x mvnw

# Build avec Maven Wrapper
RUN ./mvnw -B -DskipTests clean package

# Stage 2: Runtime léger avec JRE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port
EXPOSE 8089

# Démarrage avec profil Docker
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","/app/app.jar"]
