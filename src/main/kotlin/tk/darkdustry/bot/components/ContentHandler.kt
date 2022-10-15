package tk.darkdustry.bot.components

import arc.files.Fi
import arc.graphics.Pixmap
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
        return ImageUtils.parseImage(MapIO.generatePreview(map))
    }

    fun parseSchematic(file: File): Schematic {
        return Schematics.read(Fi(file))
    }

    fun parseSchematicImage(schematic: Schematic): ByteArray {
        val pixmap = Pixmap(schematic.width, schematic.height)
        val plans = schematic.tiles.map { stile ->
            BuildPlan(stile.x.toInt(), stile.y.toInt(), stile.rotation.toInt(), stile.block, stile.config)
        }

        // TODO

        return ImageUtils.parseImage(pixmap)
    }
}