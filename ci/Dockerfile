# Use an official OpenJDK runtime as a parent image
FROM amazoncorretto:17-alpine-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the GitHub Actions workspace into the Docker image
COPY target/recipex-0.0.1-SNAPSHOT.jar ./recipex.jar

# Expose the port on which the application runs
EXPOSE 8080

# Define the command to run the app using the JAR file
ENTRYPOINT ["java", "-jar", "./recipex.jar"]
