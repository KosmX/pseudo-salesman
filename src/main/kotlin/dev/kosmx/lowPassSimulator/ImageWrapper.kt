package dev.kosmx.lowPassSimulator

import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.math.min
import kotlin.math.roundToInt

class ImageWrapper(val width: Int, val height: Int, val scale: Double, val pencilStrength: Int = 8192, val pencilWidth: Double = 3.0, val yOffset:Int = -512) {
    val image = BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY)

    fun dot(x:Double, y:Double) {
        val x = (x * scale).roundToInt()
        val y = (y * scale).roundToInt() + yOffset
        if (x < 0 || x >= width || y < 0 || y >= height) return

        image.setRGB(
            x, y,
            min((image.getRGB(x, y) + pencilStrength), UShort.MAX_VALUE.toInt())
        )

    }

    fun save(path: File) {
        ImageIO.write(image, "png", path)
    }

}