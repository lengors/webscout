FROM gradle:jdk21-alpine AS builder
WORKDIR /workspace
COPY . .
RUN ./gradlew clean bootJar

FROM eclipse-temurin:21-jre-alpine AS run
WORKDIR /workspace
COPY --from=builder /workspace/build/libs/*.jar app.jar
COPY --from=builder /workspace/build/resources/* resources/
CMD ["-jar", "app.jar"]
ENTRYPOINT [ "java" ]