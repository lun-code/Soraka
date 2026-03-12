# Etapa 1: compilar
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el pom primero para aprovechar la caché de Docker de las dependencias
COPY backend/pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente
COPY backend/src ./src

# Compilamos saltando tests para ganar velocidad en el deploy
RUN mvn clean package -DskipTests

# Etapa 2: ejecutar
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Usamos un wildcard más específico o el nombre si lo conoces
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Configuramos el puerto dinámico de Railway (importante)
ENTRYPOINT ["java", "-jar", "app.jar"]