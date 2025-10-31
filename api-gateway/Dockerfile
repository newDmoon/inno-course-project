FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/api-gateway-1.0-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]