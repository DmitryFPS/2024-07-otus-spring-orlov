# Определяет базовый образ для этапа сборки
FROM maven:3.8.5-openjdk-17 AS build-stage

# Копируем содержимое директории src
COPY src /usr/orlov/src

# Копируем файл pom.xml
COPY pom.xml /usr/orlov

# Выполняем сборку проекта
RUN mvn -f /usr/orlov/pom.xml clean package

# Определяем базовый образ для этапа запуска приложения
FROM eclipse-temurin:17-jdk

# Копируем JAR-файл приложения
COPY --from=build-stage /usr/orlov/target/docker.jar /app/my-app.jar

# Объявляем, что контейнер будет слушать порт 8080
EXPOSE 8080

# Указываем команду, которая будет выполнена при запуске контейнера
ENTRYPOINT ["sh", "-c", "exec java -jar /app/my-app.jar"]
