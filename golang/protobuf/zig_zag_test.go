package protobuf_test

import (
	"fmt"
	"testing"

	"gitee.com/ivfzhou/my_learning_code/study_golang/protobuf"
)

func TestEncode(t *testing.T) {
	fmt.Printf("%b", protobuf.Encode(1))
}
