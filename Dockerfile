# Используем базовый образ с установленной JDK
FROM openjdk:17-jdk-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR файл в контейнер
COPY target/ImageConverter.jar /app/ImageConverter.jar

# Указываем команду для запуска Java программы
ENTRYPOINT ["java", "-jar", "/app/ImageConverter.jar"]