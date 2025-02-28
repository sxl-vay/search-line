FROM eclipse-temurin:23-jdk-alpine

LABEL authors="sxl"

COPY target/line-gateway-1.0-SNAPSHOT.jar /app/app.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]
