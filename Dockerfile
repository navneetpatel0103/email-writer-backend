# Use Eclipse Temurin JDK 21 as base image since your project uses Java 21
FROM eclipse-temurin:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy Maven Wrapper and POM file
COPY mvnw pom.xml ./
COPY .mvn/ .mvn

# Download dependencies first (caching)
RUN ./mvnw dependency:go-offline

# Copy the entire project into the container
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests

# Copy the built JAR file to the final location
RUN cp target/*.jar app.jar

# Expose the port for Render (Spring Boot default is 8080)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
