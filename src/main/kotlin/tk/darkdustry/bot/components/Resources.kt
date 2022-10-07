package tk.darkdustry.bot.components

import arc.util.*
import arc.util.serialization.Jval
import mindustry.core.Version
import tk.darkdustry.bot.dataDirectory
import java.io.IOException

object Resources {
    fun init() {
        resources()
    }

    private fun resources() {
        val repository = "https://api.github.com/repos/Anuken/Mindustry/releases/latest"

        Http.get(repository) { response ->
            val version = "v${Version.number}"
            val json: Jval = Jval.read(response.resultAsString)
            val latest: String = json.getString("tag_name").substring(1)
            val download: String = json.get("assets").asArray().get(0).getString("browser_download_url")

            if (latest == version) {
                Http.get(download) { downloadResponse ->
                    try {
                        val file = dataDirectory.child("resources/Mindustry.jar")
                        file.writeBytes(downloadResponse.result)

                        ProcessBuilder()
                            .command("unzip", file.absolutePath())
                            .redirectInput(ProcessBuilder.Redirect.INHERIT)
                            .redirectError(ProcessBuilder.Redirect.INHERIT)
                            .start()
                            .waitFor()
                    } catch (e: IOException) {
                        Log.info("Unable to unzip or download resources:\n$e")
                    }
                }
            }
        }
    }
}