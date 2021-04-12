package server

import (
	"fmt"
	"github.com/dargmuesli/gosoan/de/jonas_thelemann/uni/gosoan"
	"log"
	"net"
)

func TCPServer() {
	addrWebSocketFlatBuffers := "0.0.0.0:8470"
	addrWebSocketJson := "0.0.0.0:8474"

	listenerFlatBuffers, err := net.Listen("tcp4", addrWebSocketFlatBuffers)
	if err != nil {
		log.Fatal(err)
	}
	listenerJson, err := net.Listen("tcp4", addrWebSocketJson)
	if err != nil {
		log.Fatal(err)
	}

	log.Println("Listening TCP/FlatBuffers on: " + addrWebSocketFlatBuffers)
	log.Println("Listening TCP/Json on: " + addrWebSocketJson)

	go acceptLoop(listenerFlatBuffers, gosoan.ByteArrayToFlatBuffers)
	acceptLoop(listenerJson, gosoan.ByteArrayToJson)
}

func acceptLoop(l net.Listener, fn gosoan.ByteArrayRead) {
	defer func(l net.Listener) {
		err := l.Close()
		if err != nil {
			log.Fatal(err)
		}
	}(l)

	for {
		c, err := l.Accept()
		if err != nil {
			log.Fatal(err)
		}

		go handleConnection(c, fn)
	}
}

func handleConnection(c net.Conn, fn gosoan.ByteArrayRead) {
	fmt.Printf("New connection found! Serving %s\n", c.RemoteAddr().String())

	gosoan.ExecuteOnPrefixed(c, fn)

	err := c.Close()
	if err != nil {
		log.Fatal(err)
	}
}

