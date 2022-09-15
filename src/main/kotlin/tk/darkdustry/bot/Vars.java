package tk.darkdustry.bot;

import arc.files.Fi;
import arc.util.serialization.Json;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Vars {

    public static Config config;

    public static JDA jda;

    public static Guild guild;

    public static TextChannel mapsChannel, schematicsChannel;

    public static final Json json = new Json();
    public static final Fi dataDirectory = new Fi(".community");
}
