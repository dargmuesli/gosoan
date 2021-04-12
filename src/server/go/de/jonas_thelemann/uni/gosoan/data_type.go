package gosoan

import (
	"bufio"
	"encoding/binary"
	"encoding/json"
	"flag"
	"fmt"
	Gosoan "github.com/dargmuesli/gosoan/de/jonas_thelemann/uni/gosoan/generated"
	"io"
	"log"
)

type ByteArrayRead func([]byte) string
var counter = 0

func ByteArrayToFlatBuffers(byteArray []byte) string {
	counter++

	gosoanSensorEventFb := Gosoan.GetRootAsGosoanSensorEventFB(byteArray, 0)
	var str = fmt.Sprintf("%d FlatBuffers: %d %s %f %d %d", counter, gosoanSensorEventFb.SensorType(), gosoanSensorEventFb.SensorName(), gosoanSensorEventFb.Values(0), gosoanSensorEventFb.Accuracy(), gosoanSensorEventFb.Timestamp())

	if flag.Lookup("test.v") == nil {
		log.Println(str)
	}

	return str
}

func ByteArrayToJson(byteArray []byte) string {
	var gosoanSensorEvent SensorEvent
	var str string

	err := json.Unmarshal(byteArray, &gosoanSensorEvent)
	if err != nil {
		fmt.Println("failed to unmarshal: ", err)
		return ""
	} else {
		counter++
		str = fmt.Sprintf("%d JSON: %d %s %f %d %d", counter, gosoanSensorEvent.SensorType, gosoanSensorEvent.SensorName, gosoanSensorEvent.Values[0], gosoanSensorEvent.Accuracy, gosoanSensorEvent.Timestamp)

		if flag.Lookup("test.v") == nil {
			log.Println(str)
		}
	}

	return str
}

func ExecuteOnPrefixed(rd io.Reader, fn ByteArrayRead) {
	reader := bufio.NewReader(rd)
	bufLength := make([]byte, 4)

	for {
		_, err := io.ReadFull(reader, bufLength)
		if err != nil {
			if err == io.EOF {
				break
			}
			log.Fatal(err)
		}

		bufContent := make([]byte, binary.BigEndian.Uint32(bufLength))
		_, err = io.ReadFull(reader, bufContent)
		if err != nil {
			log.Fatal(err)
		}

		fn(bufContent)
	}
}
