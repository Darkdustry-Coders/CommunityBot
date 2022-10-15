package tk.darkdustry.bot.components

import arc.Core.atlas
import arc.Core.batch
import arc.files.ZipFi
import arc.graphics.Pixmap
import arc.graphics.Texture
import arc.graphics.g2d.TextureAtlas
import arc.graphics.g2d.TextureAtlas.AtlasRegion
import arc.graphics.g2d.TextureAtlas.TextureAtlasData
import arc.struct.Seq
import arc.util.Http
import arc.util.Log.info
import arc.util.serialization.Jval.read
import mindustry.Vars.content
import mindustry.core.ContentLoader
import mindustry.ctype.Content
import mindustry.ctype.ContentType
import mindustry.ctype.MappableContent
import tk.darkdustry.bot.resources
import tk.darkdustry.bot.sprites
import kotlin.system.measureTimeMillis

object ResourceUtils {

    fun init() {
        if (!sprites.exists()) sprites.mkdirs()

        downloadResources()

        content = ContentLoader()
        content.createBaseContent()
        for (type in ContentType.all) {
            val contents: Seq<Content> = content.getBy(type)
            contents.each { content ->
                try {
                    content.init()
                } catch (ignored: Exception) {}
            }
        }

        loadTextureDatas()

        for (type in ContentType.all) {
            val contents: Seq<Content> = content.getBy(type)
            contents.each { content ->
                try {
                    content.load()
                    content.loadIcon()
                } catch (ignored: Exception) {}
            }
        }

        atlas.setErrorRegion("error")
        batch = CustomSpriteBatch()

        loadBlockColors()
    }

    private fun downloadResources() {
        Http.get("https://api.github.com/repos/Anuken/Mindustry/releases/latest").block { release ->
            Http.get(read(release.resultAsString).get("assets").asArray().get(0).getString("browser_download_url"))
                .block { response ->
                    info("Downloading Mindustry.jar...")

                    val duration = measureTimeMillis {
                        val zip = resources.child("Mindustry.jar")
                        zip.writeBytes(response.result)

                        info("Mindustry.jar downloaded.")

                        ZipFi(zip).child("sprites").walk {
                            info("Copying ${it.name()} into ${sprites.path()}...")
                            if (it.isDirectory) it.copyFilesTo(sprites) else it.copyTo(sprites)
                        }
                    }

                    info("Downloaded {${sprites.list().size} files in ${duration}ms")
                }
        }
    }

    private fun loadTextureDatas() {
        val data = TextureAtlasData(sprites.child("sprites.atlas"), sprites, false)
        atlas = TextureAtlas()

        data.pages.each { page ->
            page.texture = Texture.createEmpty(null)
            page.texture.width = page.width
            page.texture.height = page.height
        }

        data.regions.each { region ->
            val atlasRegion = AtlasRegion(region.page.texture, region.left, region.top, region.width, region.height)
            atlasRegion.name = region.name
            atlasRegion.texture = region.page.texture

            atlas.addRegion(region.name, atlasRegion)
        }
    }

    private fun loadBlockColors() {
        val pixmap = Pixmap(sprites.child("block_colors.png"))
        for (i in 0 until pixmap.width) {
            val block = content.block(i)
            if (block.itemDrop != null) block.mapColor.set(block.itemDrop.color) else block.mapColor.rgba8888(
                pixmap.get(
                    i,
                    0
                )
            ).a(1f)
        }
        pixmap.dispose()

        info("Loaded @ block colors.", pixmap.width)
    }
}