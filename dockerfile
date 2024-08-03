FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/transaction_consumer.jar /app/transaction_consumer.jar
EXPOSE 7777
ENTRYPOINT ["java", "-jar", "/app/transaction_consumer.jar"]
