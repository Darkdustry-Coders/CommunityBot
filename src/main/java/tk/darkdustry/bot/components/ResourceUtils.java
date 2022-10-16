package tk.darkdustry.bot.components;

import arc.files.ZipFi;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureAtlas.TextureAtlasData;
import arc.graphics.g2d.TextureAtlas.TextureAtlasData.AtlasPage;
import arc.struct.ObjectMap;
import arc.util.*;
import mindustry.core.*;
import mindustry.ctype.ContentType;
import mindustry.world.Tile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static arc.Core.*;
import static arc.graphics.g2d.Draw.scl;
import static arc.graphics.g2d.Lines.useLegacyLine;
import static arc.util.Log.info;
import static arc.util.serialization.Jval.read;
import static mindustry.Vars.*;
import static tk.darkdustry.bot.Vars.*;

public class ResourceUtils {

    public static void init() {
        downloadResources();

        content = new ContentLoader();
        state = new GameState();

        content.createBaseContent();
        for (var type : ContentType.all) {
            content.getBy(type).each(content -> loadIgnoreErrors(content::init));
        }

        loadTextureDatas();

        useLegacyLine = true;
        scl = 0.25f;

        for (var type : ContentType.all) {
            content.getBy(type).each(content -> {
                loadIgnoreErrors(content::load);
                loadIgnoreErrors(content::loadIcon);
            });
        }

        loadBlockColors();

        world = new World() {
            public Tile tile(int x, int y) {
                return new Tile(x, y);
            }
        };
    }

    private static void downloadResources() {
        var mindustry = resources.child("Mindustry.jar");
        if (mindustry.exists()) return;

        Http.get("https://api.github.com/repos/Anuken/Mindustry/releases/79000151").block(release -> {
            var assets = read(release.getResultAsString()).get("assets").asArray();
            Http.get(assets.get(0).getString("browser_download_url")).block(response -> {
                info("Downloading Mindustry.jar...");
                Time.mark();

                mindustry.writeBytes(response.getResult());

                info("Mindustry.jar downloaded in @ms.", Time.elapsed());

                new ZipFi(mindustry).child("sprites").walk(fi -> {
                    info("Copying @ into @...", fi.name(), sprites.path());
                    if (fi.isDirectory()) fi.copyFilesTo(sprites);
                    else fi.copyTo(sprites);
                });

                info("Unpacked @ files.", sprites.list().length);
            });
        });
    }

    private static void loadTextureDatas() {
        var data = new TextureAtlasData(sprites.child("sprites.aatls"), sprites, false);
        var images = new ObjectMap<AtlasPage, BufferedImage>();

        atlas = new TextureAtlas();

        data.getPages().each(page -> loadIgnoreErrors(() -> {
            page.texture = Texture.createEmpty(null);
            images.put(page, ImageIO.read(page.textureFile.file()));
        }));

        data.getRegions().each(region -> atlas.addRegion(region.name, new ImageRegion(region, images.get(region.page))));

        atlas.setErrorRegion("error");
        batch = new SchematicBatch();

        info("Loaded @ pages, @ regions.", data.getPages().size, data.getRegions().size);
    }

    private static void loadBlockColors() {
        var pixmap = new Pixmap(sprites.child("block_colors.png"));
        for (int i = 0; i < pixmap.width; i++) {
            var block = content.block(i);
            if (block.itemDrop != null) block.mapColor.set(block.itemDrop.color);
            else block.mapColor.rgba8888(pixmap.get(i, 0)).a(1f);
        }
        pixmap.dispose();

        info("Loaded @ block colors.", pixmap.width);
    }

    private static void loadIgnoreErrors(UnsafeRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ignored) {}
    }
}