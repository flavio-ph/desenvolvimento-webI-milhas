# Estágio 1: Build (Compilação com JDK 21)
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copia os arquivos de build do Maven
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Baixa as dependências
RUN ./mvnw dependency:go-offline

# Copia o código-fonte e constrói o .jar
COPY src ./src
# Usa o artifactId 'milhas' e a versão do pom.xml
RUN ./mvnw clean package -DskipTests

# Estágio 2: Runtime (Execução com JRE 21)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia o .jar final do estágio de build
COPY --from=build /app/target/milhas-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]