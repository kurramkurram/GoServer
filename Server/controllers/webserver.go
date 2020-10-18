package controllers

import (
	// "encoding/json"
	"fmt"
	"net/http"
	"strconv"
	"io/ioutil"
	"os"
)

func apiSampleHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "start go server")
	fmt.Println(r)

	switch(r.Method) {
		case "POST":
			fmt.Println("POST")
			_, err := strconv.Atoi(r.Header.Get("Content-Length"))
			if err != nil {
				fmt.Printf("set correct Content-Length\n")
				w.WriteHeader(http.StatusBadRequest)
				return
			}
			file, fileHeader, err := r.FormFile ("upload_file")
			if err != nil {
				fmt.Println("can not upload")
				w.WriteHeader(http.StatusBadRequest)
				return
			}
			fmt.Println(fileHeader)
			fileName := fileHeader.Filename

			defer file.Close()
			data, err := ioutil.ReadAll(file)
			if (err != nil) {
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
	}
	w.WriteHeader(http.StatusOK)
}

func StartWebServer() error {
	http.HandleFunc("/", apiSampleHandler)
	return http.ListenAndServe(":8080", nil)
}
