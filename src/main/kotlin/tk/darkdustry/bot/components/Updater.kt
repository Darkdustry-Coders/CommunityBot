package tk.darkdustry.bot.components

import arc.util.Http
import arc.util.serialization.Jval

object Updater {
    fun update() {
        val repository = "https://api.github.com/repos/Darkdustry-Coders/CommunityBot/releases/latest"

        Http.get(repository) { response ->
            val version = this::class.java.`package`.implementationVersion
            val json: Jval = Jval.read(response.resultAsString)
            val latest: String = json.getString("tag_name").substring(1)
            val download: String = json.get("assets").asArray().get(0).getString("browser_download_url")

            if (latest == version) {

            }
        }
    }
}