package main

import (
	Gosoan "github.com/dargmuesli/gosoan/de/jonas_thelemann/uni/gosoan"
)

func main() {
	go func() {
		Gosoan.WebSocketServer()
	}()

	Gosoan.TCPServer()
}
