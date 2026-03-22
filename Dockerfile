# Estágio 1: Build
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copia apenas os arquivos de dependência primeiro (otimiza cache do Docker)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copia o código-fonte e gera o build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Estágio 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Uso de wildcard (*) para evitar erro se a versão no pom.xml mudar
COPY --from=build /app/target/*.jar app.jar

# Configurações de performance para containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]