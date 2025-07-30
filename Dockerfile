# STEP 1: Base image with Java 8
FROM openjdk:8-jdk-alpine

# STEP 2: App directory create karo container mein
VOLUME /tmp

# STEP 3: JAR file ko container mein copy karo
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# STEP 4: Port expose karo (optional, Render khud detect kar lega)
EXPOSE 8080

# STEP 5: App run karo
ENTRYPOINT ["java", "-jar", "/app.jar"]
