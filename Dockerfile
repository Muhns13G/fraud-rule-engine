FROM eclipse-temurin:25.0.3_9-jdk-ubi10-minimal AS build

WORKDIR /workspace

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src/ src/
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:25.0.3_9-jre-ubi10-minimal AS runtime

WORKDIR /app

RUN microdnf install -y shadow-utils \
    && groupadd --system spring \
    && useradd --system --gid spring --create-home spring \
    && microdnf clean all

COPY --from=build /workspace/target/fraud-rule-engine-0.0.1-SNAPSHOT.jar app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
