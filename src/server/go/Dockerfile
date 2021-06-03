# Source: https://chemidy.medium.com/create-the-smallest-and-secured-golang-docker-image-based-on-scratch-4752223b7324

FROM golang:1.16.5-alpine3.13@sha256:a8409dff6597f2ef5f7ecd3c672671bb2af9a390073efd74f95c54aa41cba22a AS stage_build
RUN apk update && apk add --no-cache git ca-certificates tzdata && update-ca-certificates
ENV USER=appuser
ENV UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    "${USER}"
WORKDIR $GOPATH/src/github.com/dargmuesli/gosoan/
COPY . .
RUN go get -d -v
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build \
    -ldflags='-w -s -extldflags "-static"' -a \
    -o /go/bin/gosoan .

FROM scratch
COPY --from=stage_build /usr/share/zoneinfo /usr/share/zoneinfo
COPY --from=stage_build /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/
COPY --from=stage_build /etc/passwd /etc/passwd
COPY --from=stage_build /etc/group /etc/group
COPY --from=stage_build /go/bin/gosoan /go/bin/gosoan
USER appuser:appuser
EXPOSE 7165
ENTRYPOINT ["/go/bin/gosoan"]