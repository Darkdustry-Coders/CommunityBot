package ru.mindustry.bot.components;

import arc.files.ZipFi;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureAtlas.TextureAtlasData;
import arc.graphics.g2d.TextureAtlas.TextureAtlasData.AtlasPage;
import arc.struct.ObjectMap;
import arc.util.*;
import mindustry.core.*;
import mindustry.world.Tile;
import ru.mindustry.bot.Vars;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static arc.Core.atlas;
import static arc.Core.batch;
import static arc.graphics.g2d.Draw.scl;
import static arc.graphics.g2d.Lines.useLegacyLine;
import static arc.util.Log.info;
import static arc.util.serialization.Jval.read;
import static mindustry.Vars.*;
import static mindustry.content.Items.*;

public class ResourceUtils {

    public static void init() {
        downloadResources();

        content = new ContentLoader();
        state = new GameState();

        content.createBaseContent();

        loadIgnoreErrors(content::init);
        loadTextureDatas();
        loadIgnoreErrors(content::load);

        loadBlockColors();
        loadItemEmojis();

        world = new World() {
            public Tile tile(int x, int y) {
                return new Tile(x, y);
            }
        };

        useLegacyLine = true;
        scl = 1f / 4f;
    }

    private static void downloadResources() {
        var mindustry = Vars.resources.child("Mindustry.jar");
        if (mindustry.exists()) return;

        Http.get("https://api.github.com/repos/Anuken/Mindustry/releases/81624846").timeout(0).block(release -> {
            var assets = read(release.getResultAsString()).get("assets").asArray();
            Http.get(assets.get(0).getString("browser_download_url")).timeout(0).block(response -> {
                info("Downloading Mindustry.jar...");
                Time.mark();

                mindustry.writeBytes(response.getResult());

                info("Mindustry.jar downloaded in @ms.", Time.elapsed());

                new ZipFi(mindustry).child("sprites").walk(fi -> {
                    info("Copying @ into @...", fi.name(), Vars.sprites.path());
                    if (fi.isDirectory()) fi.copyFilesTo(Vars.sprites);
                    else fi.copyTo(Vars.sprites);
                });

                Log.info("Unpacked @ files.", Vars.sprites.list().length);
            });
        });
    }

    private static void loadTextureDatas() {
        var data = new TextureAtlasData(Vars.sprites.child("sprites.aatls"), Vars.sprites, false);
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
        var pixmap = new Pixmap(Vars.sprites.child("block_colors.png"));
        for (int i = 0; i < pixmap.width; i++) {
            var block = content.block(i);
            if (block.itemDrop != null) block.mapColor.set(block.itemDrop.color);
            else block.mapColor.rgba8888(pixmap.get(i, 0)).a(1f);
        }
        pixmap.dispose();

        info("Loaded @ block colors.", pixmap.width);
    }

    private static void loadItemEmojis() {
        Vars.emojis.putAll(
                scrap, 1033628774664781954L,
                copper, 1033628774664781954L,
                lead, 1033628774664781954L,
                graphite, 1033628774664781954L,
                coal, 1033628774664781954L,
                titanium, 1033628774664781954L,
                thorium, 1033628774664781954L,
                silicon, 1033628774664781954L,
                plastanium, 1033628774664781954L,
                phaseFabric, 1033628774664781954L,
                surgeAlloy, 1033628774664781954L,
                sporePod, 1033628774664781954L,
                sand, 1033628774664781954L,
                blastCompound, 1033628774664781954L,
                pyratite, 1033628774664781954L,
                metaglass, 1033628774664781954L,
                beryllium, 1033628774664781954L,
                tungsten, 1033628774664781954L,
                oxide, 1033628774664781954L,
                carbide, 1033628774664781954L
        );
    }

    private static void loadIgnoreErrors(UnsafeRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ignored) {
        }
    }
}