FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /opt/app
COPY pom.xml ./
COPY ./src ./src
COPY mvnw.sh ./
RUN chmod +x mvnw.sh
RUN ./mvnw.sh -B package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar
ENTRYPOINT ["java", "-jar", "/opt/app/*.jar" ]