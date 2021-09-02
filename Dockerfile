FROM openjdk:8-jre-slim
RUN mkdir /app
COPY ./target/myapp-jar-with-dependencies.jar /app/myapp-jar-with-dependencies.jar
WORKDIR /app
CMD "java" "-jar" "myapp-jar-with-dependencies.jar"