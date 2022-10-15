package tk.darkdustry.bot.components

import arc.graphics.Pixmap
import arc.graphics.PixmapIO.PngWriter
import arc.util.io.Streams.OptimizedByteArrayOutputStream
import arc.util.io.Streams.emptyBytes

object ImageUtils {

    fun parseImage(pixmap: Pixmap): ByteArray {
        val writer = PngWriter(pixmap.width * pixmap.height)
        val stream = OptimizedByteArrayOutputStream(pixmap.width * pixmap.height)

        return try {
            writer.setFlipY(false)
            writer.write(stream, pixmap)
            stream.toByteArray()
        } catch (e: Exception) {
            emptyBytes
        } finally {
            writer.dispose()
        }
    }
}