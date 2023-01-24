package ru.mindustry.bot.components;

import static arc.graphics.Color.white;

import arc.graphics.Texture;
import arc.graphics.g2d.SpriteBatch;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Tmp;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import ru.mindustry.bot.Vars;

public class SchematicBatch extends SpriteBatch {

    public SchematicBatch() {
        super(0);
    }

    @Override
    protected void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float rotation) {
        x += 4;
        y += 4;
        x *= 4;
        y *= 4;

        var transform = new AffineTransform();
        transform.translate(x, Vars.currentImage.getHeight() - height * 4f - y);
        transform.rotate(-rotation * Mathf.degRad, originX * 4, originY * 4);

        Vars.currentGraphics.setTransform(transform);
        Vars.currentGraphics.drawImage(
            recolorImage(Vars.regions.get(((AtlasRegion) region).name)),
            0,
            0,
            (int) width * 4,
            (int) height * 4,
            null
        );
    }

    @Override
    protected void draw(Texture texture, float[] spriteVertices, int offset, int count) {}

    public BufferedImage recolorImage(BufferedImage image) {
        if (color.equals(white)) return image;

        var copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for (int x = 0; x < copy.getWidth(); x++) for (int y = 0; y < copy.getHeight(); y++) copy.setRGB(
            x,
            y,
            Tmp.c1.argb8888(image.getRGB(x, y)).mul(color).argb8888()
        );

        return copy;
    }
}
