package tk.darkdustry.bot.components;

import arc.util.Log;
import arc.util.serialization.JsonWriter;

import static tk.darkdustry.bot.Vars.*;

public class ConfigUtils {

    public static void init() {
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);

        var file = dataDirectory.child("config.json");
        if (file.exists()) {
            config = json.fromJson(Config.class, file.reader());
            Log.info("Config loaded. (@)", file.absolutePath());
        } else {
            file.writeString(json.toJson(config = new Config()));
            Log.info("Config file generated. (@)", file.absolutePath());
            System.exit(0);
        }
    }

    public static class Config {
        public String token = "token";
        public String prefix = "!";

        public long mapsChannelId = 0L;
        public long schematicsChannelId = 0L;

        public long mapsWebhookId = 0L;
        public long schematicsWebhookId = 0L;
        public String mapsWebhookToken = "token";
        public String schematicsWebhookToken = "token";
    }
}