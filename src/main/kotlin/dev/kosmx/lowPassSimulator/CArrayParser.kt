package dev.kosmx.lowPassSimulator

import dev.kosmx.pseudoSalesman.Pos
import java.nio.file.Files
import java.nio.file.Paths
import java.util.InputMismatchException
import java.util.Scanner
import java.util.regex.Pattern

fun getDataFromFile(filename:String): List<Pos> {
    val coords = mutableListOf<Pos>()
    try {
        val file = Files.newBufferedReader(Paths.get(filename))
        val scanner = Scanner(file)

        val lineBeginPattern = Pattern.compile(" *int\\s*\\w*\\[]\\s*=\\s*\\{\\s*")

        // Read the #include, comment lines before the string
        while (scanner.findInLine(lineBeginPattern) == null) {
            scanner.nextLine()
        }
        //We did find the line, now read the beginning before the numbers
        scanner.findInLine(lineBeginPattern)

        while(true) {
            try {
                val pattern = Pattern.compile("\\s*,\\s*")
                //comma deliminator
                scanner.useDelimiter(pattern)

                val x = scanner.nextInt()

                if (!scanner.hasNextInt()) break //Why scanner?!
                val y = scanner.nextInt()

                coords.add(Pos(x.toDouble(), y.toDouble())) //Add the new coordinate to our set

                if (!scanner.hasNextInt()) break
            } catch (e: InputMismatchException) {
                e.printStackTrace()
                break
            }
        }

    } catch (e:Throwable) {
        e.printStackTrace()
    }
    return coords
}
