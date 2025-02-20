# Use Eclipse Temurin JDK 21 as base image
FROM eclipse-temurin:21-jdk

# Set working directory inside the container
WORKDIR /app

# Copy Maven Wrapper and POM file
COPY mvnw pom.xml ./
COPY .mvn/ .mvn

# Give execution permission to Maven Wrapper
RUN chmod +x mvnw

# Build the dependencies to speed up builds
RUN ./mvnw dependency:go-offline

# Copy the entire project into the container
COPY src ./src
COPY src/main/resources ./src/main/resources  # Ensure resources are copied

# Build the application (Skipping tests)
RUN ./mvnw clean package -DskipTests

# Copy the built JAR file
COPY target/email-writer-0.0.1-SNAPSHOT.jar app.jar

# Expose the port for Render (Spring Boot default is 8080)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
