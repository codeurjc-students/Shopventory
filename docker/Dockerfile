FROM node:20-alpine AS angular
WORKDIR /code
COPY frontend/package*.json frontend/angular.json frontend/tsconfig*.json /code/
RUN npm install --legacy-peer-deps
COPY frontend/src /code/src
RUN npm run build -- --configuration production --base-href=/

FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /project
COPY backend/pom.xml /project/
RUN mvn dependency:go-offline -B
COPY backend/src /project/src
COPY --from=angular /code/dist/shopventory/browser /project/src/main/resources/static
RUN mvn package -DskipTests=true

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /project/target/shopventory-0.0.1-SNAPSHOT.jar /app/shopventory.jar
EXPOSE 8443
ENTRYPOINT ["java", "-jar", "shopventory.jar"]
