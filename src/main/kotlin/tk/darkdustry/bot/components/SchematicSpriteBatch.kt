package tk.darkdustry.bot.components

import arc.graphics.Pixmap
import arc.graphics.Texture
import arc.graphics.g2d.PixmapRegion
import arc.graphics.g2d.SpriteBatch
import arc.graphics.g2d.TextureRegion

class SchematicSpriteBatch : SpriteBatch(0) {

    private lateinit var currentPixmap: Pixmap

    fun setPixmap(currentPixmap: Pixmap) {
        this.currentPixmap = currentPixmap
    }

    override fun draw(
        region: TextureRegion,
        x: Float,
        y: Float,
        originX: Float,
        originY: Float,
        width: Float,
        height: Float,
        rotation: Float
    ) {
        currentPixmap.draw(PixmapRegion(region.texture.textureData.pixmap), x.toInt(), y.toInt(), originX.toInt(), originY.toInt(), width.toInt(), height.toInt())
    }

    override fun draw(texture: Texture, spriteVertices: FloatArray, offset: Int, count: Int) {}
}