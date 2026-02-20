FROM eclipse-temurin:17-jdk

WORKDIR /app

# Создаём директорию для SSL сертификатов
RUN mkdir -p /app/ssl

COPY build/libs/genocide-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]