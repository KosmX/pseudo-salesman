package dev.kosmx.pseudoSalesman

import dev.kosmx.lowPassSimulator.ImageWrapper
import dev.kosmx.lowPassSimulator.eulerSim
import dev.kosmx.lowPassSimulator.getDataFromFile
import dev.kosmx.lowPassSimulator.writeArray
import dev.kosmx.pseudoSalesman.graphUtil.connectSets
import dev.kosmx.pseudoSalesman.graphUtil.kruskal
import java.nio.file.Path
import java.nio.file.Paths

fun main(array: Array<String>) {

    val data = getDataFromFile("message.c")

    val loops = buildLoopsV2(data)

    println(loops)

    val graphEntry = kruskal(loops)
    val idk = connectSets(graphEntry)
    val posList = mutableListOf<Pos>()
    for (e in graphEntry.t.looper(0)) {
        print (e)
        posList.add(e)
    }

    writeArray(posList.iterator(), Paths.get("out.c"))


    val seq = eulerSim(posList)
    val image = ImageWrapper(2048, 1024, 8.0)

    for (x in seq) {
        image.dot(x.x, x.y)
    }

    image.save(Path.of("").resolve("out.png").toFile())

}
