# docker build -t estken/cosmos-odyssey-core:latest .
# docker push estken/cosmos-odyssey-core
FROM openjdk:24-jdk-slim
WORKDIR /app
COPY build/libs/cosmos-odyssey-core-1.0.0.jar /app/cosmos-odyssey-core.jar
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=9090
EXPOSE 9090
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/cosmos-odyssey-core.jar"]
