package dev.kosmx

import dev.kosmx.lowPassSimulator.ImageWrapper
import dev.kosmx.lowPassSimulator.eulerSim
import dev.kosmx.lowPassSimulator.getDataFromFile
import dev.kosmx.lowPassSimulator.writeArray
import dev.kosmx.pseudoSalesman.Loop
import dev.kosmx.pseudoSalesman.Looper
import dev.kosmx.pseudoSalesman.Pos
import dev.kosmx.pseudoSalesman.buildLoopsV2
import dev.kosmx.pseudoSalesman.graphUtil.connectSets
import dev.kosmx.pseudoSalesman.graphUtil.kruskal
import org.apache.commons.cli.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

val progDescription = """Pseudo salesman
    A utility for designing and simulating paths for scope arts
    Simulator: 
        A forward Euler method (simplest) programmed to act as a Low-Pass Filter
        Every parameter is configurable, the output will be drawn onto a PNG image
        -d
    Path Util:
        Identify connected shapes and tries to do the least jump between those shapes
        Uses a Kruskal algorithm
        -o to output the result as a C array
        use -s to skip it if you want to draw the original dataset with the sim tool
    
    example:
    ./pseudo-salesman.jar -i source.c -o out.c
        It will read the `source.c` file and optimize it
        
    ./pseudo-salesman.jar -i source.c -d out.png -s
        It will skip the optimization, just simulate the art from the original input
        
    You can use the simulator and path tools in the same call.
    If you do, the path optimizer will run first
    
    ./pseudo-salesman.jar -i stdin -d out.png -s -o stdout
        It will read the input from the terminal (stdin) and print the optimized array to the terminal (stdout)
        And run the simulator!
        You can use pipes < source.c > out.c
        
Args:
""".trimIndent()

val progShort = """Pseudo salesman
    For more information, use -h

Args:
""".trimIndent()

fun main(args: Array<String>) {
    val options = Options()
    val input = Option("i", "input", true ,"The array input, \"stdin\" if the input will be received in the stdin")
    val imagePath = Option("d", "draw", true, "Run simulator and draw result to a file")
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

    val iterateCount = Option("l", "loop", true, "The amount of loops before jumping, default: 0")
    val help = Option("h", "help", false, "Print the help text")

    options.addOptions(input, imagePath, imageWidth, imageHeight, xOffset, yOffset, pencilWidth, pencilStrength, imageScale, output, isIntArray, skipSalesman, simStep, tau, stepT, warmupT, help, iterateCount)


    val parser: CommandLineParser = DefaultParser()
    val formatter = HelpFormatter()

    try {
        val cmd = parser.parse(options, args)
        if (cmd.hasOption(help)) {
            println("You asked for help, you shall receive it:")
            formatter.printHelp(progDescription, options)
            exitProcess(0)
        }
        if (!cmd.hasOption(input)) {
            throw ParseException("Please specify input with -i or --input")
        }
        if (!cmd.hasOption(output) && !cmd.hasOption(imagePath)) {
            println("Please specify operation! -o or -d, can be both")
            formatter.printHelp(progShort, options)
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
            Looper.loopBeforeJump = cmd.optional(iterateCount, 0);
            val graphEntry = kruskal(buildLoopsV2(data))
            connectSets(graphEntry)
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
                it.write("\n")
            }
        }

        if (cmd.hasOption(imagePath)) {
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
            image.save(Paths.get(cmd.getOptionValue(imagePath)).toFile())
        }


    } catch (e: ParseException) {
        println(e.message);
        formatter.printHelp(progShort, options);

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