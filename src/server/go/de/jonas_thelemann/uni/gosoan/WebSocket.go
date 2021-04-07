package gosoan

import (
	"log"
	"net/http"

	"github.com/gorilla/websocket"
)

var upgrader = websocket.Upgrader{} // use default options

func WebSocketServer() {
	addrWebSocketFlatBuffers := "127.0.0.1:8770"
	addrWebSocketJson := "127.0.0.1:8774"

	log.Println("Listening WebSockets/FlatBuffers on: " + addrWebSocketFlatBuffers)
	log.Println("Listening WebSockets/Json on: " + addrWebSocketJson)

	serverMuxFlatBuffers := http.NewServeMux()
	serverMuxFlatBuffers.HandleFunc("/", echoFlatBuffers)
	serverMuxJson := http.NewServeMux()
	serverMuxJson.HandleFunc("/", echoJson)

	go func() {
		log.Fatal(http.ListenAndServe("localhost:8770", serverMuxFlatBuffers))
	}()

	log.Fatal(http.ListenAndServe("localhost:8774", serverMuxJson))
}

func echoFlatBuffers(w http.ResponseWriter, r *http.Request) {
	echo(w, r, byteArrayToFlatBuffers)
}

func echoJson(w http.ResponseWriter, r *http.Request) {
	echo(w, r, byteArrayToJson)
}

func echo(w http.ResponseWriter, r *http.Request, fn byteArrayRead) {
	c, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Print("upgrade:", err)
		return
	}
	defer c.Close()
	for {
		messageType, message, err := c.ReadMessage()
		if err != nil {
			log.Println("read:", err)
			break
		}
		fn(message)
		err = c.WriteMessage(messageType, message)
		if err != nil {
			log.Println("write:", err)
			break
		}
	}
}
