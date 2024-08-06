FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/transactionConsumer.jar /app/transactionConsumer.jar
EXPOSE 7777
ENTRYPOINT ["java", "-jar", "/app/transactionConsumer.jar"]
