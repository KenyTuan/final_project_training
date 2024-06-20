FROM maven:3.9.6 AS build
WORKDIR /app
COPY pom.xml /app
RUN mvn dependency:resolve
COPY . /app
RUN mvn clean package -DskipTests -X


FROM openjdk:17
COPY --from=build /app/target/FinalProject-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT [ "java", "-jar","app.jar" ]