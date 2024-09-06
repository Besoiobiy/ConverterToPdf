# Используем базовый образ с установленной JDK
FROM openjdk:17-jdk-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR файл в контейнер
COPY target/ImageConverter.jar /app/ImageConverter.jar
RUN mvn -f pom.xml clean package

# Указываем команду для запуска Java программы
ENTRYPOINT ["java", "-jar", "/app/ImageConverter.jar"]