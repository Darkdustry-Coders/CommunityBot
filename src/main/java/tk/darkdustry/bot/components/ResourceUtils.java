package tk.darkdustry.bot.components;

import arc.files.ZipFi;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.graphics.g2d.TextureAtlas.TextureAtlasData;
import arc.util.Http;
import arc.util.Time;
import mindustry.core.*;
import mindustry.ctype.ContentType;
import mindustry.world.Tile;

import javax.imageio.ImageIO;

import static arc.Core.*;
import static arc.graphics.g2d.Lines.useLegacyLine;
import static arc.util.Log.*;
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
            content.getBy(type).each(content -> {
                try {
                    content.init();
                } catch (Exception ignored) {}
            });
        }

        loadTextureDatas();

        for (var type : ContentType.all) {
            content.getBy(type).each(content -> {
                try {
                    content.load();
                    content.loadIcon();
                } catch (Exception ignored) {}
            });
        }

        useLegacyLine = true;
        atlas.setErrorRegion("error");

        loadBlockColors();

        world = new World() {
            public Tile tile(int x, int y) {
                return new Tile(x, y);
            }
        };
    }

    private static void downloadResources() {
        Http.get("https://api.github.com/repos/Anuken/Mindustry/releases/79000151").block(release -> {
            var assets = read(release.getResultAsString()).get("assets").asArray();
            Http.get(assets.get(0).getString("browser_download_url")).block(response -> {
                info("Downloading Mindustry.jar...");
                Time.mark();

                var zip = resources.child("Mindustry.jar");
                zip.writeBytes(response.getResult());

                info("Mindustry.jar downloaded in @ms.", Time.elapsed());

                new ZipFi(zip).child("sprites").walk(fi -> {
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

        atlas = new TextureAtlas();
        batch = new SchematicBatch();

        data.getPages().each(page -> {
            try {
                page.texture = Texture.createEmpty(null);
                page.texture.width = page.width;
                page.texture.height = page.height;

                images.put(page, ImageIO.read(page.textureFile.file()));
            } catch (Exception e) {
                err(e);
            }
        });

        data.getRegions().each(region -> {
            var atlasRegion = new AtlasRegion(region.page.texture, region.left, region.top, region.width, region.height);
            atlasRegion.name = region.name;
            atlasRegion.texture = region.page.texture;

            atlas.addRegion(region.name, atlasRegion);
            regions.put(atlasRegion, images.get(region.page));
        });

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
}