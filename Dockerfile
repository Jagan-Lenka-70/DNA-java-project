FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
COPY pom.xml pom.xml

RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -q -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app

RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup
COPY --from=build /app/target/dna-sequence-matcher-1.0.0.jar app.jar

EXPOSE 8080
USER appuser

ENTRYPOINT ["java","-jar","/app/app.jar"]
