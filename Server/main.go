package main

import (
	"fmt"
	"./controllers"
)

func main() {
	fmt.Println("start go server")
	controllers.StartWebServer()
}
