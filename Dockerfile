FROM gradle:jdk21-alpine AS builder
WORKDIR /workspace
COPY . .
RUN ./gradlew clean shadowJar
FROM eclipse-temurin:21-jre-alpine AS run
WORKDIR /workspace
COPY --from=builder /workspace/build/libs/*.jar app.jar
CMD ["-jar", "app.jar"]
ENTRYPOINT [ "java" ]