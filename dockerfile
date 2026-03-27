# -------- BUILD --------
FROM maven:3.9.13-eclipse-temurin-21 AS builder

WORKDIR /app

# Copia pom primero (cache de dependencias)
COPY pom.xml .
RUN mvn -B -q -e -DskipTests dependency:go-offline

# Copia código
COPY src ./src

# Build del jar
RUN mvn clean package -DskipTests

# -------- RUN --------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copia el jar generado
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]