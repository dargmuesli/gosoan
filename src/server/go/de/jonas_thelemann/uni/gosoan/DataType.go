package gosoan

import (
	"encoding/json"
	"fmt"
	Gosoan "github.com/dargmuesli/gosoan/de/jonas_thelemann/uni/gosoan/generated"
	"log"
)

type byteArrayRead func([]byte) string

func byteArrayToFlatBuffers(byteArray []byte) string {
	gosoanSensorEventFb := Gosoan.GetRootAsGosoanSensorEventFB(byteArray, 0)
	log.Printf("recv FlatBuffers: %d %s %f %d %d", gosoanSensorEventFb.SensorType(), gosoanSensorEventFb.SensorName(), gosoanSensorEventFb.Values(0), gosoanSensorEventFb.Accuracy(), gosoanSensorEventFb.Timestamp())
	return string(byteArray)
}

func byteArrayToJson(byteArray []byte) string {
	var gosoanSensorEvent GosoanSensorEvent

	if err := json.Unmarshal(byteArray, &gosoanSensorEvent); err != nil {
		fmt.Println("failed to unmarshal: ", err)
	} else {
		log.Printf("recv JSON: %d %s %f %d %d", gosoanSensorEvent.SensorType, gosoanSensorEvent.SensorName, gosoanSensorEvent.Values[0], gosoanSensorEvent.Accuracy, gosoanSensorEvent.Timestamp)
	}

	return string(byteArray)
}
