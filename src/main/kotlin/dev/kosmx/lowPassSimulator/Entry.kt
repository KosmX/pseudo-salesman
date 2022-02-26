package dev.kosmx.lowPassSimulator

import java.io.File
import java.nio.file.Path
import javax.imageio.ImageIO

fun main(args: Array<String>) {

    val data = getDataFromFile("message.c")
    println(data)

    val seq = eulerSim(data)
    val image = ImageWrapper(2048, 1024, 8.0)
    for (x in seq) {
        image.dot(x.x, x.y)
    }

    image.save(Path.of("").resolve("out.png").toFile())

}


