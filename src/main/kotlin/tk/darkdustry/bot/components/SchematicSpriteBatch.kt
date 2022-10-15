package tk.darkdustry.bot.components

import arc.graphics.Pixmap
import arc.graphics.Texture
import arc.graphics.g2d.SpriteBatch
import arc.graphics.g2d.TextureAtlas.AtlasRegion
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage


class SchematicSpriteBatch : SpriteBatch(0) {

    lateinit var currentImage: BufferedImage
    lateinit var currentGraphics: Graphics2D

    override fun draw(region: TextureRegion, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, rotation: Float) {
        val at = AffineTransform()
        at.translate((x + 4).toDouble() * 4, currentImage.height - (y + 4).toDouble() * 4 - height * 4)
        at.rotate(-rotation * Mathf.degRad.toDouble(), originX.toDouble() * 4, originY.toDouble() * 4)

        currentGraphics.transform = at
        currentGraphics.drawImage(null, 0, 0, width.toInt() * 4, height.toInt() * 4, null)
    }


    override fun draw(texture: Texture, spriteVertices: FloatArray, offset: Int, count: Int) {}
}