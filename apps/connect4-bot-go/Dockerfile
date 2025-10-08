# syntax=docker/dockerfile:1

FROM golang:1.19
WORKDIR /app
COPY go.mod ./
RUN go mod download
COPY *.go ./
RUN CGO_ENABLED=0 GOOS=linux go build -o /connect4-bot-go

EXPOSE 8080

ENTRYPOINT ["/connect4-bot-go"]
CMD ["1", "1"]
