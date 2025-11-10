FROM maven:3.9-eclipse-temurin-17-alpine

WORKDIR /app
COPY . .

# Собираем
RUN mvn clean package -DskipTests

# Смотрим что собралось
RUN find /app/target -name "*.jar" -type f

# Запускаем найденный JAR
CMD java -jar $(find /app/target -name "*.jar" -type f | head -n 1)