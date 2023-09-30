FROM eclipse-temurin:17-jdk-jammy

# Setting up work directory
WORKDIR /app

# Copy the jar file into our app
COPY ./target/*.jar /app

# Exposing port 8080
EXPOSE 8080

# Starting the application
CMD ["java", "-jar", "*.jar"]