package tk.darkdustry.bot.components

import arc.Core.batch
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
import java.io.File

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
        val pixmap = Pixmap(schematic.width, schematic.height)
        val plans = schematic.tiles.map { stile ->
            BuildPlan(stile.x.toInt(), stile.y.toInt(), stile.rotation.toInt(), stile.block, stile.config)
        }

        (batch as SchematicSpriteBatch).setPixmap(pixmap)

        Draw.reset()
        plans.each { plan ->
            plan.animScale = 1f
            plan.worldContext = false
            plan.block.drawPlanRegion(plan, plans)
            Draw.reset()
        }

        plans.each { plan -> plan.block.drawPlanConfigTop(plan, plans) }

        return parseImage(pixmap)
    }

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