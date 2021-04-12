package main

import (
	"github.com/dargmuesli/gosoan/de/jonas_thelemann/uni/gosoan/server"
)

func main() {
	go func() {
		server.WebSocketServer()
	}()

	server.TCPServer()
}
