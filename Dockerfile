# Use Eclipse Temurin JDK 21 as base image
FROM eclipse-temurin:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven

# Copy POM file and download dependencies (caching)
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy the entire project into the container
COPY . .

# Build the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# Copy the built JAR file to the final location
RUN cp target/*.jar app.jar

# Expose the port for Render (Spring Boot default is 8080)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
