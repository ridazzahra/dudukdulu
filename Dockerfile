# Build: Maven + JDK 17  ->  Run: Tomcat 10.1 (namespace jakarta.*)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

FROM tomcat:10.1-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/foodorder.war /usr/local/tomcat/webapps/ROOT.war
# Konfigurasi lewat environment variable (lihat README): DB_URL, DB_USER, DB_PASSWORD, UPLOAD_DIR
ENV UPLOAD_DIR=/data/uploads
# Batasi heap agar muat di hosting gratis berRAM 512 MB
ENV CATALINA_OPTS="-Xms64m -Xmx320m"
RUN mkdir -p /data/uploads
EXPOSE 8080
