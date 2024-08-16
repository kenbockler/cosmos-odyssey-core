# docker build -t cosmos-odyssey-core:latest .
# docker tag cosmos-odyssey-core estken/cosmos-odyssey-core
# docker push estken/cosmos-odyssey-core
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/cosmos-odyssey-core-1.0.0.jar /app/cosmos-odyssey-core.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=9090
EXPOSE 9090
ENTRYPOINT ["java","-jar","app.jar"]