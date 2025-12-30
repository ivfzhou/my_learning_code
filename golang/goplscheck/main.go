package main

import (
	"flag"
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
	"sort"
	"strings"
)

var (
	skipSuffix string
	directory  string
	verbose    bool
)

func init() {
	flag.StringVar(&skipSuffix, "s", "", "skip files in directory")
	flag.StringVar(&directory, "d", "./", "directory want to check")
	flag.BoolVar(&verbose, "v", false, "verbose mode")
	flag.Parse()
}

func main() {
	if isFile(directory) {
		if strings.HasSuffix(directory, ".go") || strings.HasSuffix(directory, "go.mod") {
			checkGoFile("./", directory)
		}
		return
	}

	allGoFiles := seekFiles(directory)
	sort.Strings(allGoFiles)

	for _, v := range allGoFiles {
		checkGoFile(directory, v)
	}
}

func seekFiles(dir string) []string {
	allGoFiles := make([]string, 0, 100)
	var fn func(dir string)
	fn = func(dir string) {
		fileEntries, err := os.ReadDir(dir)
		if err != nil {
			panic(err)
		}
		for _, entry := range fileEntries {
			if entry.IsDir() {
				fn(filepath.Join(dir, entry.Name()))
				continue
			}
			fileName := entry.Name()
			if strings.HasSuffix(fileName, ".go") || fileName == "go.mod" {
				allGoFiles = append(allGoFiles, filepath.Join(dir, fileName))
			}
		}
	}
	fn(dir)
	return allGoFiles
}

func checkGoFile(workDirectory, filePath string) {
	if len(skipSuffix) > 0 && strings.HasSuffix(filePath, skipSuffix) {
		if verbose {
			fmt.Printf("%s: skiped\n", filePath)
		}
		return
	}

	command := exec.Command("gopls", "check", "--severity=hint", filePath)
	command.Dir = workDirectory
	output, err := command.CombinedOutput()
	if err != nil {
		panic(fmt.Errorf("%s: %v %s\n", filePath, err, output))
	}
	if len(output) > 0 {
		fmt.Printf("%s", output)
	} else {
		if verbose {
			fmt.Printf("%s: passed\n", filePath)
		}
	}
}

func isFile(filePath string) bool {
	fileInfo, err := os.Stat(filePath)
	if err != nil {
		return false
	}
	return !fileInfo.IsDir()
}
