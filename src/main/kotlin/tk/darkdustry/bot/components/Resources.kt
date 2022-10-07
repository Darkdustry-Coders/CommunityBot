package tk.darkdustry.bot.components

import arc.files.ZipFi
import arc.util.Http
import arc.util.Log.info
import arc.util.serialization.Jval
import tk.darkdustry.bot.*
import kotlin.system.measureTimeMillis

object Resources {
    fun init() {
        check()

        // Потом грузит спрайты в бота
    }

    private fun check() {
        info("Checking resources...")
        val repository = "https://api.github.com/repos/Anuken/Mindustry/releases/latest"

        Http.get(repository) { response ->
            val json: Jval = Jval.read(response.resultAsString)
            val download: String = json.get("assets").asArray().get(0).getString("browser_download_url")

            Http.get(download) { downloadResponse ->
                info("Updating resources")

                val duration = measureTimeMillis {
                    try {
                        if (!sprites.exists()) sprites.mkdirs()
                        val file = dataDirectory.child("resources/Mindustry.jar")
                        file.writeBytes(downloadResponse.result)

                        info("Mindustry.jar downloaded.")

                        val zip = ZipFi(file)
                        zip.child("sprites").walk {
                            info("Copying ${it.name()} into ${sprites.path()}...")
                            if (it.isDirectory) it.copyFilesTo(sprites) else it.copyTo(sprites)
                        }
                    } catch (e: Exception) {
                        info("Unable to unzip or download resources:\n$e")
                    }
                }

                info("Done in ${duration}ms")
            }
        }
    }
}