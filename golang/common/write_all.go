package common

import "io"

type WriteAll struct {
	w io.Writer
}

func NewWriteAll(w io.Writer) io.Writer {
	return &WriteAll{w: w}
}

func (w WriteAll) Write(p []byte) (n int, err error) {
	for len(p) > n {
		var written int
		written, err = w.w.Write(p[n:])
		n += written
		if err != nil {
			return
		}
	}
	return
}
