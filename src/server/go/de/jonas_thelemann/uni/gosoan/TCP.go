package gosoan

import (
	"bufio"
	"encoding/binary"
	"fmt"
	"io"
	"log"
	"net"
)

func TCPServer() {
	addrWebSocketFlatBuffers := "127.0.0.1:8470"
	addrWebSocketJson := "127.0.0.1:8474"

	listenerFlatBuffers, err := net.Listen("tcp4", addrWebSocketFlatBuffers)
	if err != nil {
		fmt.Println(err)
		return
	}
	listenerJson, err := net.Listen("tcp4", addrWebSocketJson)
	if err != nil {
		fmt.Println(err)
		return
	}

	//defer listenerFlatBuffers.Close()
	//defer listenerJson.Close()

	log.Println("Listening TCP/FlatBuffers on: " + addrWebSocketFlatBuffers)
	log.Println("Listening TCP/Json on: " + addrWebSocketJson)

	go acceptLoop(listenerFlatBuffers, byteArrayToFlatBuffers)
	acceptLoop(listenerJson, byteArrayToJson)
}

func acceptLoop(l net.Listener, fn byteArrayRead) {
	defer l.Close()
	for {
		c, err := l.Accept()
		if err != nil {
			log.Fatal(err)
		}
		fmt.Println("New connection found!")
		go handleConnection(c, fn)
	}
}

func handleConnection(c net.Conn, fn byteArrayRead) {
	fmt.Printf("Serving %s\n", c.RemoteAddr().String())

	reader := bufio.NewReader(c)
	bufLength := make([]byte, 4)

	for {
		_, err := io.ReadFull(reader, bufLength)
		if err != nil {
			fmt.Println(err)
			return
		}

		bufContent := make([]byte, binary.BigEndian.Uint32(bufLength))
		_, err = io.ReadFull(reader, bufContent)
		if err != nil {
			fmt.Println(err)
			return
		}

		fn(bufContent)
	}

	c.Close()
}
