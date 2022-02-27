package dev.kosmx.lowPassSimulator

import dev.kosmx.pseudoSalesman.Pos
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.util.Arrays
import javax.imageio.ImageIO
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class ImageWrapper(val width: Int, val height: Int, val scale: Double, val pencilStrength: Int = 2, val pencilWidth: Double = 2.0, val yOffset:Int = -512) {
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    fun dot(x:Double, y:Double) {
        val x = (x * scale).roundToInt()
        val y = (y * scale).roundToInt() + yOffset
        if (x < 0 || x >= width || y < 0 || y >= height) return
        //internalDot(x, y, pencilStrength)
        dotNeighbour(x, y)
    }

    private fun internalDot(x: Int, y: Int, s:Double) {
        if (x < 0 || x >= width || y < 0 || y >= height) return
        val max = 0xffffff
        val color = Color(image.getRGB(x, y))
        val oldR = color.red
        val oldG = color.green
        val oldB = color.blue

        val newR = min(oldR + (s * 4).toInt(), 0xff)
        val newG = min(oldG + (s * 8).toInt(), 0xff)
        val newB = min(oldB + (s * 1).toInt(), 0xff)
        val newColor = Color(newR, newG, newB)

        image.setRGB(x, y, newColor.rgb)

    }

    private fun dotNeighbour(x: Int, y:Int) {
        for (dx in -pencilWidth.toInt()..pencilWidth.toInt()) {
            for (dy in -pencilWidth.toInt()..pencilWidth.toInt()) {
                internalDot(x + dx, y + dy, easeOutCirc( 1-((Pos(dx.toDouble(), dy.toDouble()) distance Pos.ZERO) / pencilWidth)).let {
                    (it * pencilStrength)
                })
            }
        }
    }

    fun save(path: File) {
        ImageIO.write(image, "png", path)
    }

}
fun easeOutCirc(x: Double): Double {
    if (x < 0) return .0
    if (x > 1) return 1.0
    return sqrt(1 - (x - 1).pow(2));
}