package dev.kosmx.lowPassSimulator

import dev.kosmx.pseudoSalesman.Pos
import java.io.BufferedReader
import java.io.BufferedWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.InputMismatchException
import java.util.Scanner
import java.util.regex.Pattern
import kotlin.math.roundToInt

fun getDataFromFile(filename:String, isCArray: Boolean = true): List<Pos> {
    Files.newBufferedReader(Paths.get(filename)).use {
        return getDataFromFile(it, isCArray)
    }
}


fun getDataFromFile(reader: BufferedReader, isCArray: Boolean): List<Pos> {
    val coords = mutableListOf<Pos>()
    try {
        val scanner = Scanner(reader)

        if (isCArray) {
            val lineBeginPattern = Pattern.compile(" *int\\s*\\w*\\[]\\s*=\\s*\\{\\s*")

            // Read the #include, comment lines before the string
            while (scanner.findInLine(lineBeginPattern) == null) {
                scanner.nextLine()
            }
            //We did find the line, now read the beginning before the numbers
            scanner.findInLine(lineBeginPattern)
        }
        while (true) {
            try {
                val pattern = Pattern.compile("\\s*,\\s*")
                //comma deliminator
                scanner.useDelimiter(pattern)

                val x = scanner.nextInt()

                if (!scanner.hasNextInt()) break //Why scanner?!
                val y = scanner.nextInt()

                coords.add(Pos(x.toDouble(), y.toDouble())) //Add the new coordinate to our set

                if (!scanner.hasNextInt()) break
            } catch (_: java.lang.IllegalStateException) {
                //just ignore if the input is closed
            } catch (e: InputMismatchException) {
                e.printStackTrace()
                break
            }
        }


    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return coords
}

fun writeArray(posIt: Iterator<Pos>, writer: BufferedWriter) {
    writer.write("int shape[] = {")

    while (posIt.hasNext()) {
        val pos = posIt.next()
        val x = pos.x.roundToInt()
        val y = pos.y.roundToInt()
        writer.write("${x}, ${y}")

        if (posIt.hasNext()) writer.write(", ")
    }
    writer.write("};")

}

fun writeArray(posIt: Iterator<Pos>, file: Path) {

    try {
        val writer = Files.newBufferedWriter(file)
        writeArray(posIt, writer)

    } catch (e: Throwable) {
        e.printStackTrace()
    }

}
