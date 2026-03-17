FROM node:20-alpine AS frontend-build
WORKDIR /workspace/Frontend

COPY Frontend/package*.json ./
RUN npm ci

COPY Frontend/ ./
RUN npm run build

FROM maven:3.9.9-eclipse-temurin-21 AS backend-build
WORKDIR /workspace/Backend

COPY Backend/pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

COPY Backend/ ./
RUN mkdir -p src/main/resources/static
COPY --from=frontend-build /workspace/Frontend/dist/ src/main/resources/static/
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=backend-build /workspace/Backend/target/backend-0.1.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
