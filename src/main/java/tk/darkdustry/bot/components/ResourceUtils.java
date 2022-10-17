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
import mindustry.world.Tile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static arc.Core.*;
import static arc.graphics.g2d.Draw.scl;
import static arc.graphics.g2d.Lines.useLegacyLine;
import static arc.util.Log.info;
import static arc.util.serialization.Jval.read;
import static mindustry.Vars.*;
import static mindustry.content.Items.*;
import static tk.darkdustry.bot.Vars.*;

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
        var mindustry = resources.child("Mindustry.jar");
        if (mindustry.exists()) return;

        Http.get("https://api.github.com/repos/Anuken/Mindustry/releases/79000151").timeout(Integer.MAX_VALUE).block(release -> {
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

    private static void loadItemEmojis() {
        emojis.putAll(
                scrap, 770045449750577192L,
                copper, 770045449603645492L,
                lead, 770045449846521856L,
                graphite, 770045449729343488L,
                coal, 770045449582411817L,
                titanium, 770045449822142554L,
                thorium, 770045449612558366L,
                silicon, 770045449696182302L,
                plastanium, 801022400211976243L,
                phaseFabric, 770045449326821413L,
                surgeAlloy, 770045449700507668L,
                sporePod, 770045449692250123L,
                sand, 770045449758441502L,
                blastCompound, 770045449654108211L,
                pyratite, 770045449335209985L,
                metaglass, 770045449834463242L,
                beryllium, 972298068097662987L,
                tungsten, 962490016506994708L,
                oxide, 973958882563063891L,
                carbide, 973958957909573673L
        );
    }

    private static void loadIgnoreErrors(UnsafeRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ignored) {}
    }
}