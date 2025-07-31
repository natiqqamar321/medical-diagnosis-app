# Step 1: Build the app
FROM maven:3.8.1-openjdk-8 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run the app
FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/medical-diagnosis-app.war app.war
EXPOSE 8080
ENV PORT=8080
ENTRYPOINT ["java", "-jar", "app.war"]
