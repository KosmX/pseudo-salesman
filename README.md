# Pseudo-salesman  
**Featuring: recursive spaghetti in Kotlin**

---

This is a pseudo solver for [Travelling Salesman problem](https://en.wikipedia.org/wiki/Travelling_salesman_problem)  
Designed to optimize path for [simonyiszk/scopelogo](https://github.com/simonyiszk/scopelogo)

---

An [ESP](https://www.espressif.com/en/products/socs/esp32) unit is jumping between pre-programmed keypoints and a [Low-pass filter](https://en.wikipedia.org/wiki/Low-pass_filter) is smoothing the dost to lines  
But without optimization, there is too much jumping between characters.  

This project is to minimize that jumping


## Usage
```shell
java -jar pseudo-salesman.jar
```
Or if you have `jarwrapper`
```shell
./pseudo-salesman.jar
```  

```shell
usage: Pseudo salesman
    A utility for designing and simulating paths for scope arts
    Simulator:
        A forward Euler method (simplest) programmed to act as a Low-Pass
              Filter
        Every parameter is configurable, the output will be drawn onto a
              PNG image
        -d
    Path Util:
        Identify connected shapes and tries to do the least jump between
              those shapes
        Uses a Kruskal algorithm
        -o to output the result as a C array
        use -s to skip it if you want to draw the original dataset with
              the sim tool

    example:
    ./pseudo-salesman.jar -i source.c -o out.c
        It will read the `source.c` file and optimize it

    ./pseudo-salesman.jar -i source.c -d out.png -s
        It will skip the optimization, just simulate the art from the
              original input

    You can use the simulator and path tools in the same call.
    If you do, the path optimizer will run first

    ./pseudo-salesman.jar -i stdin -d out.png -s -o stdout
        It will read the input from the terminal (stdin) and print the
              optimized array to the terminal (stdout)
        And run the simulator!
        You can use pipes < source.c > out.c

Args:
 -a,--plainArray                    The input is *NOT* a C array/C file,
                                    do not look for "int shape[] = {...}"
 -d,--draw <arg>                    Run simulator and draw result to a
                                    file
 -f,--simulatorStep <arg>           The simulation time step, default:
                                    0.008t
 -h,--help                          Print the help text
 -i,--input <arg>                   The array input, "stdin" if the input
                                    will be received in the stdin
 -o,--output <arg>                  The result array, can be "stdout"
 -pencil,--pencilWidth <arg>        Pencil radius in pixels, default 2.0px
 -s,--skipOptimization              Skip the optimization, useful if you
                                    want to see the simulator output
                                    without modifying the sample order
 -scale,--imageScale <arg>          The image scale factor, default: 8
 -strength,--pencilStrength <arg>   Pencil strength, default 2bit
 -t,--stepTime <arg>                The input feeding speed, default:
                                    10.0t
 -tau,--timeConstant <arg>          The time constant of the LPF, default:
                                    5.0t
 -w,--width <arg>                   Width of the image, default: 2048px
 -warmup,--warmupTime <arg>         Simulation time before turning on the
                                    output for one cycle, default: 100.0t
 -x,--horizontalOffset <arg>        horizontal offset..., default: 0px
 -y,--verticalOffset <arg>          vertical offset, default: -512px
```