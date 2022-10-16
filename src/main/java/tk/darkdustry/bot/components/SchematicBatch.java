package tk.darkdustry.bot.components;

import arc.graphics.Texture;
import arc.graphics.g2d.SpriteBatch;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;

import java.awt.geom.AffineTransform;

import static tk.darkdustry.bot.Vars.*;

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
        transform.translate(x, currentImage.getHeight() - height - y);
        transform.rotate(-rotation * Mathf.degRad, originX * 4, originY * 4);

        currentGraphics.setTransform(transform);
        currentGraphics.drawImage(regions.get(region), 0, 0, (int) width * 4, (int) height * 4, null);
    }

    @Override
    protected void draw(Texture texture, float[] spriteVertices, int offset, int count) {}
}