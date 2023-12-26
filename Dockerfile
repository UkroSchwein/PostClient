FROM gradle:8.3-jdk20-alpine
WORKDIR /app
COPY ./ ./
CMD ["./mvnw", "spring-boot:run"]
