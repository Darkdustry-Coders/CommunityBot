package tk.darkdustry.bot.components

import arc.graphics.Texture
import arc.graphics.g2d.TextureAtlas.AtlasRegion
import java.awt.image.BufferedImage

class ImageRegion(texture: Texture, x: Int, y: Int, image: BufferedImage) : AtlasRegion() {

    var image: BufferedImage

    init {
        AtlasRegion(texture, x, y, image.width, image.height)
        this.image = image
    }
}