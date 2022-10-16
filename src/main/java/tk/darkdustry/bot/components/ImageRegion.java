package tk.darkdustry.bot.components;

import arc.graphics.g2d.TextureAtlas.TextureAtlasData.Region;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;

import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static tk.darkdustry.bot.Vars.*;

public class ImageRegion extends AtlasRegion {

    public BufferedImage image;

    public ImageRegion(Region region) {
        super(region.page.texture, region.left, region.top, region.width, region.height);

        this.name = region.name;
        this.texture = region.page.texture;

        var image = new BufferedImage(region.width, region.height, TYPE_INT_ARGB);
        var graphics = image.createGraphics();

        graphics.drawImage(images.get(region.page), 0, 0, region.width, region.height, region.left, region.top, region.left + region.width, region.top + region.height, null);

        regions.put(name, image);
    }
}