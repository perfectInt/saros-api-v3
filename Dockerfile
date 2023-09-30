FROM eclipse-temurin:17-jdk-jammy as builder
COPY . .
RUN chmod +x mvnw.sh
RUN ./mvnw.sh -B package

FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080
COPY --from=builder target/*.jar *.jar
ENTRYPOINT ["java", "-jar", "*.jar" ]