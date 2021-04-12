package server

import (
	"github.com/dargmuesli/gosoan/de/jonas_thelemann/uni/gosoan"
	"log"
	"net/http"

	"github.com/gorilla/websocket"
)

var upgrader = websocket.Upgrader{}

func WebSocketServer() {
	addrWebSocketFlatBuffers := "0.0.0.0:8770"
	addrWebSocketJson := "0.0.0.0:8774"

	log.Println("Listening WebSockets/FlatBuffers on: " + addrWebSocketFlatBuffers)
	log.Println("Listening WebSockets/Json on: " + addrWebSocketJson)

	serverMuxFlatBuffers := http.NewServeMux()
	serverMuxFlatBuffers.HandleFunc("/", handleRequestFlatBuffers)
	serverMuxJson := http.NewServeMux()
	serverMuxJson.HandleFunc("/", handleRequestJson)

	go func() {
		log.Fatal(http.ListenAndServe(addrWebSocketFlatBuffers, serverMuxFlatBuffers))
	}()

	log.Fatal(http.ListenAndServe(addrWebSocketJson, serverMuxJson))
}

func handleRequestFlatBuffers(w http.ResponseWriter, r *http.Request) {
	handleRequest(w, r, gosoan.ByteArrayToFlatBuffers)
}

func handleRequestJson(w http.ResponseWriter, r *http.Request) {
	handleRequest(w, r, gosoan.ByteArrayToJson)
}

func handleRequest(w http.ResponseWriter, r *http.Request, fn gosoan.ByteArrayRead) {
	c, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Print("upgrade:", err)
		return
	}
	defer func(c *websocket.Conn) {
		err := c.Close()
		if err != nil {
			log.Fatal(err)
		}
	}(c)

	for {
		_, message, err := c.ReadMessage()
		if err != nil {
			log.Print(err)
			break
		}

		fn(message)
	}
}
