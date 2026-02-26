package main

import (
	"fmt"
	"math/rand"
	"os"
)

/*
 * 1 字节编码: 0xxx_xxxx，7 位，码点范围：[0, 127] 0x0~0x7f
 * 2 字节编码: 110x_xxxx 10xx_xxxx，11 位，码点范围：[128, 2047] 0x80~0x7ff
 * 3 字节编码: 1110_xxxx 10xx_xxxx 10xx_xxxx，16 位，码点范围：[2048, 65535] 0x800~0xffff
 * 4 字节编码: 1111_0xxx 10xx_xxxx 10xx_xxxx 10xx_xxxx，21 位，码点范围：[65536, 2097151] 0x10000~0x1fffff
 *
 * 汉 码点：0x6c49 编码值：0xe6b189(0b_1110_0110, 0b_1011_0001, 0b_1000_1001)
 */

func main() {
	fmt.Println(string(RandomUTF8Character()))
	PrintHan()
	// PrintUTF8CharacterEncodingValueSections()
	// PrintUTF8CharactersAndCodePoint()
	// PrintUTF8CharactersAndEncodingValue()
}

const (
	mask                  = uint32(1)
	oneByteMaximumValue   = uint32(0b01111111)
	twoByteMinimumValue   = uint32(0b11000000_10000000)
	twoByteMaximumValue   = uint32(0b11011111_10111111)
	threeByteMinimumValue = uint32(0b11100000_10000000_10000000)
	threeByteMaximumValue = uint32(0b11101111_10111111_10111111)
	fourByteMinimumValue  = uint32(0b11110000_10000000_10000000_10000000)
	fourByteMaximumValue  = uint32(0b11110111_10111111_10111111_10111111)
)

type Section struct {
	Begin, End uint32
}

func PrintHan() {
	fmt.Println(string([]rune{rune(0x6c49)}))
	fmt.Println(string(rune(0x6c49)))
	fmt.Printf("%c\n", rune(0x6c49))
	fmt.Println("\u6c49")
}

func PrintUTF8CharactersAndCodePoint() {
	fileStream, err := os.OpenFile("./utf8_all_characters.txt", os.O_WRONLY|os.O_CREATE|os.O_TRUNC, 0644)
	if err != nil {
		panic(err)
	}
	defer func() {
		if fileStream != nil {
			_ = fileStream.Close()
		}
	}()
	counter := 1

	for i := rune(0x0); i <= rune(0x7f); i++ {
		_, _ = fmt.Fprintf(fileStream, "%c(%X)", i, i)
		counter++
		if counter%10 == 0 {
			_, _ = fmt.Fprintf(fileStream, "\n")
		} else {
			_, _ = fmt.Fprintf(fileStream, "  ")
		}
	}
	for i := rune(0x80); i <= rune(0x7ff); i++ {
		_, _ = fmt.Fprintf(fileStream, "%c(%X)", i, i)
		counter++
		if counter%10 == 0 {
			_, _ = fmt.Fprintf(fileStream, "\n")
		} else {
			_, _ = fmt.Fprintf(fileStream, "  ")
		}
	}
	for i := rune(0x800); i <= rune(0xffff); i++ {
		_, _ = fmt.Fprintf(fileStream, "%c(%X)", i, i)
		counter++
		if counter%10 == 0 {
			_, _ = fmt.Fprintf(fileStream, "\n")
		} else {
			_, _ = fmt.Fprintf(fileStream, "  ")
		}
	}
	for i := rune(0x10000); i <= rune(0x1fffff); i++ {
		_, _ = fmt.Fprintf(fileStream, "%c(%X)", i, i)
		counter++
		if counter%10 == 0 {
			_, _ = fmt.Fprintf(fileStream, "\n")
		} else {
			_, _ = fmt.Fprintf(fileStream, "  ")
		}
	}
}

func PrintUTF8CharactersAndEncodingValue() {
	sections := GetUTF8CharacterSections()
	fileStream, err := os.OpenFile("./utf8_all_encoding_characters.txt", os.O_WRONLY|os.O_CREATE|os.O_TRUNC, 0644)
	if err != nil {
		panic(err)
	}
	defer func() {
		if fileStream != nil {
			_ = fileStream.Close()
		}
	}()
	counter := 1
	for _, section := range sections {
		for i := section.Begin; i <= section.End; i++ {
			bs := ConvertRuneToBytes(rune(i))
			for len(bs) > 0 {
				n, err2 := fileStream.Write(bs)
				if err2 != nil {
					panic(err2)
				}
				bs = bs[n:]
			}
			_, _ = fmt.Fprintf(fileStream, "(%X)", i)
			counter++
			if counter%10 == 0 {
				_, _ = fmt.Fprintf(fileStream, "\n")
			} else {
				_, _ = fmt.Fprintf(fileStream, "  ")
			}
		}
	}
}

func PrintUTF8CharacterEncodingValueSections() {
	fileStream, err := os.OpenFile("./utf8_encoding_sections.txt", os.O_CREATE|os.O_TRUNC|os.O_WRONLY, 0644)
	if err != nil {
		panic(err)
	}
	defer func() {
		if fileStream != nil {
			_ = fileStream.Close()
		}
	}()
	for _, v := range GetUTF8CharacterSections() {
		_, _ = fmt.Fprintf(fileStream, "[%d, %d]\n", v.Begin, v.End)
	}
}

func GetUTF8CharacterSections() []*Section {
	var sections []*Section
	flag := false
	begin := uint32(0)
	for code := range fourByteMaximumValue {
		if IsValidUTF8CharacterEncode(code) {
			if !flag {
				flag = true
				begin = code
			}
		} else {
			if flag {
				flag = false
				sections = append(sections, &Section{Begin: begin, End: code - 1})
			}
		}
	}
	if flag {
		sections = append(sections, &Section{Begin: begin, End: fourByteMaximumValue})
	}

	for _, v := range sections {
		if v.Begin >= v.End {
			panic(fmt.Errorf("end smaller than begin %d %d", v.Begin, v.End))
		}
	}

	for i := 0; i < len(sections)-1; i++ {
		if sections[i+1].Begin <= sections[i].End {
			panic(fmt.Errorf("previous end smaller than next begin %d %d", sections[i+1].Begin, sections[i].End))
		}
	}

	return sections
}

func IsValidUTF8CharacterEncode(code uint32) bool {
	switch {
	case code <= oneByteMaximumValue:
		if !IsBitOff(code, 24+0) {
			return false
		}
	case code >= twoByteMinimumValue && code <= twoByteMaximumValue:
		if !IsBitOn(code, 16+0) || !IsBitOn(code, 16+1) || !IsBitOff(code, 16+2) ||
			!IsBitOn(code, 16+8) || !IsBitOff(code, 16+9) {
			return false
		}
	case code >= threeByteMinimumValue && code <= threeByteMaximumValue:
		if !IsBitOn(code, 8+0) || !IsBitOn(code, 8+1) || !IsBitOn(code, 8+2) || !IsBitOff(code, 8+3) ||
			!IsBitOn(code, 8+8) || !IsBitOff(code, 8+9) ||
			!IsBitOn(code, 8+16) || !IsBitOff(code, 8+17) {
			return false
		}
	case code >= fourByteMinimumValue && code <= fourByteMaximumValue:
		if !IsBitOn(code, 0) || !IsBitOn(code, 1) || !IsBitOn(code, 2) || !IsBitOn(code, 3) || !IsBitOff(code, 4) ||
			!IsBitOn(code, 8) || !IsBitOff(code, 9) ||
			!IsBitOn(code, 16) || !IsBitOff(code, 17) ||
			!IsBitOn(code, 24) || !IsBitOff(code, 25) {
			return false
		}
	default:
		return false
	}
	return true
}

func ConvertRuneToBytes(r rune) []byte {
	const mask = uint32(0b11111111)
	code := uint32(r)
	result := make([]byte, 0, 4)
	b := uint8((code >> 24) & mask)
	if b > 0 {
		result = append(result, b)
	}
	b = uint8((code >> 16) & mask)
	if b > 0 {
		result = append(result, b)
	}
	b = uint8((code >> 8) & mask)
	if b > 0 {
		result = append(result, b)
	}
	b = uint8(code & mask)
	result = append(result, b)
	return result
}

func IsBitOn(code uint32, index int) bool {
	return (code>>(31-index))&mask == 1
}

func IsBitOff(code uint32, index int) bool {
	return (code>>(31-index))&mask == 0
}

func RandomUTF8Character() rune {
	switch rand.Intn(4) {
	case 0:
		return rune(rand.Int63n(0x7f + 1))
	case 1:
		return rune(rand.Int63n(0x7ff-0x80+1) + 0x80)
	case 2:
		return rune(rand.Int63n(0xffff-0x800+1) + 0x800)
	case 3:
		return rune(rand.Int63n(0x1fffff-0x10000+1) + 0x10000)
	}
	panic("unreachable")
}
