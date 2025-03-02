FROM eclipse-temurin:23-jdk-alpine

LABEL authors="sxl"

COPY target/search-line-es-core-service-0.0.1-SNAPSHOT.jar /app/app.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

EXPOSE 8214

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} --add-opens java.base/java.lang=ALL-UNNAMED -jar /app/app.jar"]
