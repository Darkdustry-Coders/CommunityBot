package ru.mindustry.bot;

import static arc.util.Log.err;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MEMBERS;
import static net.dv8tion.jda.api.requests.GatewayIntent.MESSAGE_CONTENT;
import static net.dv8tion.jda.internal.requests.RestActionImpl.setDefaultFailure;
import static ru.mindustry.bot.Vars.*;

import net.dv8tion.jda.api.JDABuilder;
import ru.mindustry.bot.components.ConfigUtils;
import ru.mindustry.bot.components.ResourceUtils;

public class Main {

    public static void main(String[] args) {
        cache.delete();

        dataDirectory.mkdirs();
        cache.mkdirs();
        resources.mkdirs();
        sprites.mkdirs();

        ConfigUtils.init();
        ResourceUtils.init();

        setDefaultFailure(null);

        try {
            jda =
                JDABuilder
                    .createLight(config.token)
                    .enableIntents(GUILD_MEMBERS, MESSAGE_CONTENT)
                    .addEventListeners(new Listener())
                    .build()
                    .awaitReady();

            guild = jda.getGuildById(config.guildId);
            mapsChannel = jda.getTextChannelById(config.mapsChannelId);
            schematicsChannel = jda.getTextChannelById(config.schematicsChannelId);
        } catch (Exception e) {
            err("Failed to launch the bot. Make sure the provided token and guild/channel IDs in the configuration are correct.");
            err(e);
        }

        Listener.registerCommands();
    }
}
