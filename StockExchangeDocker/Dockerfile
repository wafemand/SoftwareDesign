FROM gradle:jdk15 as builder

WORKDIR /app
COPY . .

ENV PORT=9090
ENV DB_CONNECTION="mongodb://host.docker.internal:27017"

EXPOSE ${PORT}

CMD ./gradlew run --args="${PORT} ${DB_CONNECTION}"