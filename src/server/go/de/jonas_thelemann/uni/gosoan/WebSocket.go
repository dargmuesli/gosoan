package gosoan

import (
	"log"
	"net/http"

	Gosoan "github.com/dargmuesli/gosoan/de/jonas_thelemann/uni/gosoan/generated"
	"github.com/gorilla/websocket"
)

var upgrader = websocket.Upgrader{} // use default options

func echo(w http.ResponseWriter, r *http.Request) {
	c, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Print("upgrade:", err)
		return
	}
	defer c.Close()
	for {
		mt, message, err := c.ReadMessage()
		if err != nil {
			log.Println("read:", err)
			break
		}
		gosoanSensorEvent := Gosoan.GetRootAsGosoanSensorEvent(message, 0)
		log.Printf("recv: %d %s %f %d %d", gosoanSensorEvent.SensorType(), gosoanSensorEvent.SensorName(), gosoanSensorEvent.Values(0), gosoanSensorEvent.Accuracy(), gosoanSensorEvent.Timestamp())
		err = c.WriteMessage(mt, message)
		if err != nil {
			log.Println("write:", err)
			break
		}
	}
}

func WebSocketServer() {
	http.HandleFunc("/echo", echo)
	log.Fatal(http.ListenAndServe("0.0.0.0:8783", nil))
}
