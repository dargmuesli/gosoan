package gosoan

type GosoanSensorEvent struct {
	SensorType int `json:"sensorType"`
	SensorName string  `json:"sensorName"`
	Values []float32  `json:"values"`
	Accuracy int  `json:"accuracy"`
	Timestamp int64  `json:"timestamp"`
}
