# Use the official Eclipse Temurin image as a parent image
FROM eclipse-temurin:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the packaged jar file into the container
COPY build/libs/lv-0.0.1-SNAPSHOT.jar app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file with the 'local' profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=local"]
