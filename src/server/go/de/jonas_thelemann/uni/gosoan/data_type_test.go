package gosoan

import (
	"os"
	"path/filepath"
	"testing"
)

func benchmark(b *testing.B, path string, fn ByteArrayRead) {
	abs, err := filepath.Abs(path)
	if err != nil {
		b.Error(err)
		return
	}

	file, err := os.Open(abs)
	if err != nil {
		b.Error(err)
		return
	}

	b.ResetTimer()

	ExecuteOnPrefixed(file, fn)

	err = file.Close()
	if err != nil {
		b.Error(err)
		return
	}
}

func BenchmarkFlatBuffers(b *testing.B) {
	benchmark(b, "../../../../../../test/test_data_flatbuffers", ByteArrayToFlatBuffers)
}

func BenchmarkJson(b *testing.B) {
	benchmark(b, "../../../../../../test/test_data_json", ByteArrayToJson)
}