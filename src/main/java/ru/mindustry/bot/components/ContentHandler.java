package ru.mindustry.bot.components;

import static arc.util.io.Streams.emptyBytes;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static ru.mindustry.bot.Vars.currentGraphics;
import static ru.mindustry.bot.Vars.currentImage;

import arc.files.Fi;
import arc.graphics.Pixmap;
import arc.graphics.PixmapIO.PngWriter;
import arc.graphics.g2d.Draw;
import arc.util.io.Streams.OptimizedByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import mindustry.graphics.Drawf;
import mindustry.io.MapIO;
import mindustry.maps.Map;

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
        var image = new BufferedImage(schematic.width * 32 + 64, schematic.height * 32 + 64, TYPE_INT_ARGB);
        var plans = schematic.tiles.map(stile -> new BuildPlan(stile.x + 1, stile.y + 1, stile.rotation, stile.block, stile.config));

        currentImage = image;
        currentGraphics = image.createGraphics();

        Draw.reset();

        for (int x = 0; x < schematic.width + 2; x++) for (int y = 0; y < schematic.height + 2; y++) Draw.rect(
            "metal-floor",
            x * 8f,
            y * 8f
        );

        plans.each(plan -> Drawf.squareShadow(plan.drawx(), plan.drawy(), plan.block.size * 16f, 1f));

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
