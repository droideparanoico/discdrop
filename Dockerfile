# DiscDrop — Quarkus multi-stage build

# ── Stage 1: Build ──────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml ./
COPY src ./src
RUN mvn package -DskipTests

# ── Stage 2: Runtime ────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/target/quarkus-app/ /app/
VOLUME /data
EXPOSE 8080
USER discdrop
ENTRYPOINT ["java", "-XX:MinHeapFreeRatio=10", "-XX:MaxHeapFreeRatio=20", "-jar", "quarkus-run.jar"]