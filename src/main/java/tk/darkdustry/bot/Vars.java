package tk.darkdustry.bot;

import arc.files.Fi;
import arc.graphics.g2d.TextureAtlas.TextureAtlasData.AtlasPage;
import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectMap;
import arc.util.serialization.Json;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import tk.darkdustry.bot.components.ConfigUtils.Config;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Vars {
    public static Config config;

    public static JDA jda;
    public static Guild guild;

    public static TextChannel mapsChannel, schematicsChannel;

    public static final Json json = new Json();

    public static final Fi dataDirectory = Fi.get(".community");
    public static final Fi cache = dataDirectory.child("cache");
    public static final Fi resources = dataDirectory.child("resources");
    public static final Fi sprites = dataDirectory.child("sprites");

    public static final ObjectMap<AtlasPage, BufferedImage> images = new ObjectMap<>();
    public static final ObjectMap<TextureRegion, BufferedImage> regions = new ObjectMap<>();

    public static BufferedImage currentImage;
    public static Graphics2D currentGraphics;
}