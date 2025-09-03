# Multi-stage build để optimize size
FROM gradle:8.7-jdk21-alpine AS builder
WORKDIR /app

# Copy gradle files trước để cache dependencies
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Download dependencies (layer caching)
RUN gradle dependencies --no-daemon

# Copy source code và build
COPY src ./src
RUN gradle bootJar --no-daemon

# Runtime stage - sử dụng image nhẹ hơn
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Tạo user non-root cho security
RUN addgroup -g 1001 -S spring && \
    adduser -u 1001 -S spring -G spring

# Copy JAR từ builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Chuyển sang user non-root
USER spring:spring

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]