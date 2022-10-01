package tk.darkdustry.bot.components

import arc.func.Prov
import arc.graphics.*
import arc.util.io.*
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.io.*
import mindustry.maps.Map
import mindustry.world.*
import java.io.*
import java.util.zip.InflaterInputStream

object MapUtils {
    fun <T> notNullElse(value: T, defaultValue: T): T {
        return value ?: defaultValue
    }

    fun renderMinimap(): ByteArray {
        return parseImage(MapIO.generatePreview(Vars.world.tiles), false)
    }

    fun renderMap(map: Map): ByteArray {
        return try {
            parseImage(generatePreview(map), true)
        } catch (e: Exception) {
            Streams.emptyBytes
        }
    }

    fun parseImage(pixmap: Pixmap, flip: Boolean): ByteArray {
        val writer = PixmapIO.PngWriter(pixmap.width * pixmap.height)
        val stream = Streams.OptimizedByteArrayOutputStream(pixmap.width * pixmap.height)
        return try {
            writer.setFlipY(flip)
            writer.write(stream, pixmap)
            stream.toByteArray()
        } catch (e: Exception) {
            Streams.emptyBytes
        } finally {
            writer.dispose()
        }
    }

    @Throws(IOException::class)
    private fun generatePreview(map: Map): Pixmap {
        CounterInputStream(InflaterInputStream(map.file.read(Vars.bufferSize))).use { counter ->
            DataInputStream(counter).use { stream ->
                SaveIO.readHeader(stream)

                val version = FixedSave(stream.readInt())
                val pixmap = Pixmap(map.width, map.height)
                val tile: ContainerTile = AdvancedContainerTile(pixmap)

                version.region("meta", stream, counter) { metaStream: DataInput -> version.readStringMap(metaStream) }
                version.region("content", stream, counter) { contentStream: DataInput ->
                    version.readContentHeader(
                        contentStream
                    )
                }
                version.region("preview_map", stream, counter) { input: DataInput ->
                    version.readMap(input, object : WorldContext {
                        override fun resize(width: Int, height: Int) {}
                        override fun isGenerating(): Boolean {
                            return false
                        }

                        override fun begin() {}
                        override fun end() {}
                        override fun onReadBuilding() {
                            val size = tile.block().size
                            val offset = -(size - 1) / 2
                            for (x in 0 until size) for (y in 0 until size) pixmap[tile.x + x + offset, tile.y + y + offset] =
                                tile.team.color.rgba8888()
                        }

                        override fun tile(index: Int): Tile {
                            tile.x = (index % map.width).toShort()
                            tile.y = (index / map.width).toShort()
                            return tile
                        }

                        override fun create(x: Int, y: Int, floorID: Int, overlayID: Int, wallID: Int): Tile? {
                            pixmap[x, y] = MapIO.colorFor(
                                Blocks.air,
                                Vars.content.block(floorID),
                                Vars.content.block(overlayID),
                                Team.derelict
                            )
                            return null
                        }
                    })
                }
                return pixmap
            }
        }
    }

    open class AdvancedContainerTile(val pixmap: Pixmap) : ContainerTile() {
        override fun setBlock(block: Block) {
            super.setBlock(block)
            val color = MapIO.colorFor(block, Blocks.air, Blocks.air, notNullElse(team, Team.derelict))
            if (color != Color.blackRgba) pixmap[x.toInt(), y.toInt()] = color
        }
    }

    open class ContainerTile : CachedTile() {
        internal var team = Team.derelict

        override fun setTeam(team: Team) {
            this.team = team
        }

        override fun setBlock(block: Block) {
            this.block = block
        }

        override fun changeBuild(team: Team, entityprov: Prov<Building>, rotation: Int) {}
        override fun changed() {}
    }

    class FixedSave(version: Int) : SaveVersion(version) {
        @Throws(IOException::class)
        override fun readMap(stream: DataInput, context: WorldContext) {
            val width = stream.readUnsignedShort()
            val height = stream.readUnsignedShort()

            run {
                var i = 0
                while (i < width * height) {
                    val floorID = stream.readShort()
                    val oreID = stream.readShort()
                    val consecutive = stream.readUnsignedByte()
                    for (j in i..i + consecutive) context.create(
                        j % width,
                        j / width,
                        floorID.toInt(),
                        oreID.toInt(),
                        0
                    )
                    i += consecutive
                    i++
                }
            }

            var i = 0
            while (i < width * height) {
                val block = notNullElse(Vars.content.block(stream.readShort().toInt()), Blocks.air)
                val tile = context.tile(i)
                val packedCheck = stream.readByte()
                val hadEntity = packedCheck.toInt() and 1 != 0
                val hadData = packedCheck.toInt() and 2 != 0
                val isCenter = !hadEntity || stream.readBoolean()

                if (isCenter || hadData) {
                    tile.setBlock(block)
                }

                if (hadEntity) {
                    if (!isCenter) {
                        i++
                        continue
                    }

                    if (block.hasBuilding()) readChunk(stream, true) { input: DataInput ->
                        input.skipBytes(6)
                        tile.setTeam(Team.get(input.readByte().toInt()))
                        input.skipBytes(lastRegionLength - 7)
                    } else skipChunk(stream, true)

                    context.onReadBuilding()
                } else {
                    val consecutive = stream.readUnsignedByte()
                    for (j in i + 1..i + consecutive) context.tile(j).setBlock(block)
                    i += consecutive
                }

                i++
            }
        }
    }
}