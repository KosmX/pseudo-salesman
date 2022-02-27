package dev.kosmx

import dev.kosmx.lowPassSimulator.ImageWrapper
import dev.kosmx.lowPassSimulator.eulerSim
import dev.kosmx.lowPassSimulator.getDataFromFile
import dev.kosmx.lowPassSimulator.writeArray
import dev.kosmx.pseudoSalesman.Pos
import dev.kosmx.pseudoSalesman.buildLoopsV2
import dev.kosmx.pseudoSalesman.graphUtil.kruskal
import org.apache.commons.cli.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    val options = Options()
    val input = Option("i", "input", true ,"The array input, \"stdin\" if the input will be received in the stdin").apply { this.isRequired = true }
    val image = Option("d", "draw", true, "Run simulator and draw result to a file")
    val imageWidth = Option("w", "width", true, "Width of the image, default: 2048px")
    val imageHeight = Option("h", "height", true, "Height of the image, default: 1024px")
    val xOffset = Option("x", "horizontalOffset", true, "horizontal offset..., default: 0px")
    val yOffset = Option("y", "verticalOffset", true, "vertical offset, default: -512px")
    val pencilWidth = Option("pencil", "pencilWidth", true, "Pencil radius in pixels, default 2.0px")
    val pencilStrength= Option("strength", "pencilStrength", true, "Pencil strength, default 2bit")
    val imageScale = Option("scale", "imageScale", true, "The image scale factor, default: 8")

    val output = Option("o", "output", true, "The result array, can be \"stdout\"")

    val isIntArray = Option("a", "plainArray", false,"The input is *NOT* a C array/C file, do not look for \"int shape[] = {...}\"")
    val skipSalesman = Option("s", "skipOptimization", false, "Skip the optimization, useful if you want to see the simulator output without modifying the sample order")

    val simStep = Option("f", "simulatorStep", true, "The simulation time step, default: 0.008t")
    val tau = Option("tau", "timeConstant", true, "The time constant of the LPF, default: 5.0t")
    val stepT = Option("t", "stepTime", true, "The input feeding speed, default: 10.0t")
    val warmupT = Option("warmup", "warmupTime", true, "Simulation time before turning on the output for one cycle, default: 100.0t")

    options.addOptions(input, image, imageWidth, imageHeight, xOffset, yOffset, pencilWidth, pencilStrength, imageScale, output, isIntArray, skipSalesman, simStep, tau, stepT, warmupT)


    val parser: CommandLineParser = DefaultParser()
    val formatter = HelpFormatter()

    try {
        val cmd = parser.parse(options, args);
        if (!cmd.hasOption(output) && !cmd.hasOption(image)) {
            println("And what should I do with it?")
            formatter.printHelp("utility-name", options)
            exitProcess(2)
        }

        val reader: BufferedReader = cmd.getOptionValue(input).let {
            if (it == "stdin") BufferedReader(InputStreamReader(System.`in`)) else Files.newBufferedReader(Paths.get(it))
        }
        var data: List<Pos> = mutableListOf<Pos>()
        reader.use {
            data = getDataFromFile(it, !cmd.hasOption(isIntArray))
        }

        if (!cmd.hasOption(skipSalesman)) {
            val graphEntry = kruskal(buildLoopsV2(data))
            val tmpList = mutableListOf<Pos>()
            for (e in graphEntry.t.looper(0)) {
                tmpList.add(e)
            }

            data = tmpList
        }

        if (cmd.hasOption(output)) {
            val writer = cmd.getOptionValue(output).let {
                if (it == "stdout") BufferedWriter(OutputStreamWriter(System.out)) else Files.newBufferedWriter(Paths.get(it))
            }
            writer.use {
                writeArray(data.iterator(), it)
            }
        }

        if (cmd.hasOption(image)) {
            val image = ImageWrapper(cmd.optional(imageWidth, 2048), cmd.optional(imageWidth, 1024),
                scale = cmd.optional(imageScale, 8.0),
                xOffset = cmd.optional(xOffset, 0),
                yOffset = cmd.optional(yOffset, -512),
                pencilStrength = cmd.optional(pencilStrength, 2),
                pencilWidth = cmd.optional(pencilWidth, 2.0)
            )

            val seq = eulerSim(data,
                simStep = cmd.optional(simStep, 0.008),
                tau = cmd.optional(tau, 5.0),
                stepT = cmd.optional(stepT, 10.0),
                tWarmup = cmd.optional(warmupT, 100.0)
            )

            for (dot in seq) {
                image.dot(dot.x, dot.y)
            }
            image.save(Paths.get(cmd.getOptionValue(imageHeight)).toFile())
        }


    } catch (e: ParseException) {
        println(e.message);
        formatter.printHelp("utility-name", options);

        exitProcess(1);
    }
}

fun Options.addOptions(vararg option: Option) {
    for(arg in option) {
        this.addOption(arg)
    }
}

fun CommandLine.optional(option: Option, default: Int):Int {
    if (this.hasOption(option)) {
        try {
            return this.getOptionValue(option).toInt()
        } catch (e: java.lang.NumberFormatException) {
            throw ParseException("${option.argName} must be an integer")
        }
    }
    return default
}

fun CommandLine.optional(option: Option, default: Double):Double {
    if (this.hasOption(option)) {
        try {
            return this.getOptionValue(option).toDouble()
        } catch (e: java.lang.NumberFormatException) {
            throw ParseException("${option.argName} must be a number")
        }
    }
    return default
}