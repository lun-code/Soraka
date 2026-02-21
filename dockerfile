# Usar Java 21
FROM eclipse-temurin:21-jdk-jammy

# Directorio de trabajo
WORKDIR /app

# Copiar el JAR compilado
COPY backend/target/*.jar app.jar

# Exponer puerto de la app
EXPOSE 8080

# Ejecutar la app
ENTRYPOINT ["java","-jar","app.jar"]
