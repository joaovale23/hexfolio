# Etapa 1: build com o Maven Wrapper
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -B -q dependency:go-offline
COPY src src
RUN ./mvnw -B -q package -DskipTests && \
    java -Djarmode=tools -jar target/hexfolio-*.jar extract --layers --launcher --destination target/extracted

# Etapa 2: só o runtime, em camadas para o cache do Docker
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --uid 1001 hexfolio
USER hexfolio
COPY --from=build /app/target/extracted/dependencies/ ./
COPY --from=build /app/target/extracted/spring-boot-loader/ ./
COPY --from=build /app/target/extracted/snapshot-dependencies/ ./
COPY --from=build /app/target/extracted/application/ ./
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "org.springframework.boot.loader.launch.JarLauncher"]
