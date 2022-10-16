package tk.darkdustry.bot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import tk.darkdustry.bot.components.ConfigUtils;
import tk.darkdustry.bot.components.ResourceUtils;

import static arc.util.Log.err;
import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static tk.darkdustry.bot.Vars.*;

public class Main {

    public static void main(String[] args) {
        cache.delete();

        dataDirectory.mkdirs();
        cache.mkdirs();
        resources.mkdirs();
        sprites.mkdirs();

        ConfigUtils.init();
        ResourceUtils.init();

        try {
            jda = JDABuilder.createLight(config.token)
                    .enableIntents(GUILD_MEMBERS, MESSAGE_CONTENT)
                    .addEventListeners(new Listener())
                    .build()
                    .awaitReady();

            guild = jda.getGuildById(config.guildId);
            assert guild != null;

            mapsChannel = guild.getTextChannelById(config.mapsChannelId);
            schematicsChannel = guild.getTextChannelById(config.schematicsChannelId);
            assert mapsChannel != null && schematicsChannel != null;

            RestActionImpl.setDefaultFailure(null);
        } catch (Exception e) {
            err("Failed to launch Community Bot. Make sure the provided token and guild/channel IDs in the configuration are correct.");
            err(e);
        }
    }
}