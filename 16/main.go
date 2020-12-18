package main

import ("fmt")

func main() {


    arr := []int32{97, 127, 157, 179, 131, 61}

    var m int32 = 1
    for _, a := range arr {
        m *= a
    }
    fmt.Println(m)

}