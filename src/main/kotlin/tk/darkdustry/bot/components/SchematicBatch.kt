package tk.darkdustry.bot.components

import arc.graphics.Texture
import arc.graphics.g2d.SpriteBatch
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf.degRad
import arc.util.Tmp
import tk.darkdustry.bot.currentGraphics
import tk.darkdustry.bot.currentImage
import java.awt.geom.AffineTransform


class SchematicBatch : SpriteBatch(0) {
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
        val tx = x * 4 + 16
        val ty = currentImage!!.height - height - y * 4 - 16

        val at = AffineTransform()
        at.translate(tx.toDouble(), ty.toDouble())

        at.rotate(-rotation.toDouble() * degRad, originX.toDouble() * 4, originY.toDouble() * 4)
        currentGraphics!!.transform = at

        val image = (region as ImageRegion).image
        for (x in 0 until image.width)
            for (y in 0 until image.height)
                image.setRGB(x, y, Tmp.c1.argb8888(image.getRGB(x, y)).mul(color).argb8888())

        currentGraphics!!.drawImage(image, 0, 0, (width * 4).toInt(), (height * 4).toInt(), null)
    }

    override fun draw(texture: Texture?, spriteVertices: FloatArray?, offset: Int, count: Int) {}
}