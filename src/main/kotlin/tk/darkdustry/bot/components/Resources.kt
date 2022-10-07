package tk.darkdustry.bot.components

import arc.files.ZipFi
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
            val json: Jval = Jval.read(response.resultAsString)
            val download: String = json.get("assets").asArray().get(0).getString("browser_download_url")

            Http.get(download) { downloadResponse ->
                try {
                    val file = dataDirectory.child("resources/Mindustry.jar")
                    file.writeBytes(downloadResponse.result)

                    Log.info("Mindustry.jar downloaded.")

                    val zip = ZipFi(file)

                    zip.child("sprites").walk {
                        Log.info(it.name())
                    }

                } catch (e: IOException) {
                    Log.info("Unable to unzip or download resources:\n$e")
                }
            }
        }
    }
}