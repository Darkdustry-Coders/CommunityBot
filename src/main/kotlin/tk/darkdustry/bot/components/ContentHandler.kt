package tk.darkdustry.bot.components

import arc.files.Fi
import arc.graphics.Pixmap
import arc.graphics.PixmapIO.PngWriter
import arc.graphics.g2d.Draw
import arc.util.io.Streams.OptimizedByteArrayOutputStream
import arc.util.io.Streams.emptyBytes
import mindustry.entities.units.BuildPlan
import mindustry.game.Schematic
import mindustry.game.Schematics
import mindustry.io.MapIO
import mindustry.maps.Map
import tk.darkdustry.bot.currentGraphics
import tk.darkdustry.bot.currentImage
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO

object ContentHandler {

    fun parseMap(file: File): Map {
        return MapIO.createMap(Fi(file), true)
    }

    fun parseMapImage(map: Map): ByteArray {
        return parseImage(MapIO.generatePreview(map))
    }

    fun parseSchematic(file: File): Schematic {
        return Schematics.read(Fi(file))
    }

    fun parseSchematicImage(schematic: Schematic): ByteArray {
        val image = BufferedImage(schematic.width * 32, schematic.height * 32, TYPE_INT_ARGB)
        val plans = schematic.tiles.map { stile ->
            BuildPlan(stile.x.toInt(), stile.y.toInt(), stile.rotation.toInt(), stile.block, stile.config)
        }

        currentImage = image
        currentGraphics = image.createGraphics()

        plans.each { plan ->
            plan.animScale = 1f
            plan.worldContext = false
            plan.block.drawPlanRegion(plan, plans)
            Draw.reset()
        }

        plans.each { plan -> plan.block.drawPlanConfigTop(plan, plans) }

        return parseImage(image)
    }

    private fun parseImage(pixmap: Pixmap): ByteArray {
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

    private fun parseImage(image: BufferedImage): ByteArray {
        val stream = ByteArrayOutputStream()

        return try {
            ImageIO.write(image, "png", stream)
            stream.toByteArray()
        } catch (e: Exception) {
            emptyBytes
        }
    }
}