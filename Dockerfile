FROM node:18

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    apt-get clean;

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew clean build --no-daemon

RUN cp build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]