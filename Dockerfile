# Runtime léger Java 17
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# On ne fige pas le nom du JAR : on copie le jar généré par Maven
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose le port de ton app (modifie si besoin)
EXPOSE 8080

# Démarrage
ENTRYPOINT ["java","-jar","/app/app.jar"]
