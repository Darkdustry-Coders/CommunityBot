package tk.darkdustry.bot.components;

import arc.files.Fi;
import arc.graphics.Pixmap;
import arc.graphics.PixmapIO.PngWriter;
import arc.graphics.g2d.Draw;
import arc.util.io.Streams.OptimizedByteArrayOutputStream;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import mindustry.io.MapIO;
import mindustry.maps.Map;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import static arc.util.io.Streams.emptyBytes;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static tk.darkdustry.bot.Vars.*;

public class ContentHandler {

    public static Map parseMap(File file) throws IOException {
        return MapIO.createMap(new Fi(file), true);
    }

    public static byte[] parseMapImage(Map map) throws IOException {
        return parseImage(MapIO.generatePreview(map));
    }

    public static Schematic parseSchematic(File file) throws IOException {
        return Schematics.read(new Fi(file));
    }

    public static byte[] parseSchematicImage(Schematic schematic) {
        var image = new BufferedImage(schematic.width * 32, schematic.height * 32, TYPE_INT_ARGB);
        var plans = schematic.tiles.map(stile -> new BuildPlan(stile.x, stile.y, stile.rotation, stile.block, stile.config));

        currentImage = image;
        currentGraphics = image.createGraphics();

        Draw.reset();

        plans.each(plan -> {
            plan.animScale = 1f;
            plan.worldContext = false;
            plan.block.drawPlanRegion(plan, plans);
            Draw.reset();
        });

        plans.each(plan -> plan.block.drawPlanConfigTop(plan, plans));

        return parseImage(image);
    }

    private static byte[] parseImage(Pixmap pixmap) {
        var writer = new PngWriter(pixmap.width * pixmap.height);
        var stream = new OptimizedByteArrayOutputStream(pixmap.width * pixmap.height);

        try {
            writer.setFlipY(false);
            writer.write(stream, pixmap);
            return stream.toByteArray();
        } catch (Exception e) {
            return emptyBytes;
        } finally {
            writer.dispose();
        }
    }

    private static byte[] parseImage(BufferedImage image) {
        var stream = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, "png", stream);
            return stream.toByteArray();
        } catch (Exception e) {
            return emptyBytes;
        }
    }
}