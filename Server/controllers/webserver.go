package controllers

import (
	"fmt"
	"io/ioutil"
	"net/http"
	"os"
	"strconv"
	"strings"
)

const TWO_HYPHON = "--"
const LINE_END = "\r\n"

func apiSampleHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println(r)

	switch r.Method {
	case "POST":
		fmt.Println("POST")
		_, err := strconv.Atoi(r.Header.Get("Content-Length"))
		if err != nil {
			fmt.Println("set correct Content-Length")
			w.WriteHeader(http.StatusBadRequest)
			return
		}
		file, fileHeader, err := r.FormFile("upload_file")
		if err != nil {
			fmt.Println("can not upload")
			w.WriteHeader(http.StatusBadRequest)
			return
		}
		fmt.Println(fileHeader)
		fileName := fileHeader.Filename
		fmt.Println(fileName)

		defer file.Close()
		data, err := ioutil.ReadAll(file)
		if err != nil {
			fmt.Println("file error")
			w.WriteHeader(http.StatusBadRequest)
			return
		}
		fmt.Println(string(data))
		saveFile, err := os.Create(fileName)
		if err != nil {
			fmt.Println("can not create file")
			w.WriteHeader(http.StatusBadRequest)
			return
		}
		defer saveFile.Close()

		_, err = saveFile.Write(data)
		if err != nil {
			fmt.Println("can not write file")
			w.WriteHeader(http.StatusBadRequest)
			return
		}

	case "GET":
		fmt.Println("GET")
		name := fmt.Sprint(r.URL)
		fileName := strings.SplitAfter(name, "/")[1]
		fmt.Println(fileName)
		if fileinfo, err := os.Stat("./" + fileName); os.IsNotExist(err) || fileinfo.IsDir() {
			fmt.Println("file is not exist")
			w.WriteHeader(http.StatusNotFound)
			return
		}
		buf, err := ioutil.ReadFile("./" + fileName)
		if err != nil {
			fmt.Println("file could not be read")
			w.WriteHeader(http.StatusInternalServerError)
			return
		}
		fmt.Println(string(buf))
		w.Write(buf)
	}
	w.WriteHeader(http.StatusOK)
}

func StartWebServer() error {
	http.HandleFunc("/", apiSampleHandler)
	return http.ListenAndServe(":8080", nil)
}
