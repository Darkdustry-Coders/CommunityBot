package tk.darkdustry.bot.components

import arc.util.Http
import arc.util.serialization.Jval

object Updater {
    fun update() {
        val repository = "https://api.github.com/repos/Darkdustry-Coders/CommunityBot/releases/latest"
        val mindustry = "https://api.github.com/repos/Anuken/Mindustry/releases/latest"

        Http.get(repository) { res ->
            val version = this::class.java.`package`.implementationVersion
            val json: Jval = Jval.read(res.resultAsString)
            val latest: String = json.getString("tag_name").substring(1)
            val download: String = json.get("assets").asArray().get(0).getString("browser_download_url")

            if (download == version) {

            }
        }
    }

    fun resources() {

    }
}