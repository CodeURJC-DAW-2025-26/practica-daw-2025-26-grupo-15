#------FRONTEND BUILD PART-------
FROM node:22 AS frontend-builder
WORKDIR /frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

#------PROJECT COMPILATION PART-------

# Base image for the build container
FROM maven:3.9-eclipse-temurin-21 AS builder
# Define the working directory to execute commands
WORKDIR /backend
# Copy the project code
COPY backend/pom.xml .
# Download project dependencies
RUN mvn dependency:go-offline
# Copy the project code
COPY backend .
# Copy frontend build into backend static resources
COPY --from=frontend-builder /frontend/build/client/ /backend/src/main/resources/static/new/
# Compile the project and download libraries
RUN mvn -B package -DskipTests


#------PROJECT BUILD AND EXECUTION PART-------
# Base image for the application container
FROM eclipse-temurin:21-jre
# Define the working directory where the JAR is located
WORKDIR /app
# Copy the JAR from the build container
COPY --from=builder /backend/target/*.jar app.jar
# Indicate the port exposed by the container
EXPOSE 8443
# Command executed when running docker run
CMD [ "java", "-jar", "app.jar" ] 