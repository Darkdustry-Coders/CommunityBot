package tk.darkdustry.bot

import arc.files.Fi
import arc.struct.ObjectMap
import arc.util.serialization.Json
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import tk.darkdustry.bot.components.ConfigUtils.Config
import java.awt.Graphics2D
import java.awt.image.BufferedImage

lateinit var config: Config

lateinit var jda: JDA
lateinit var guild: Guild

lateinit var mapsChannel: TextChannel
lateinit var schematicsChannel: TextChannel

val json = Json()

val dataDirectory = Fi(".community")
val cache: Fi = dataDirectory.child("cache")
val resources: Fi = dataDirectory.child("resources")
val sprites: Fi = dataDirectory.child("sprites")

var currentImage: BufferedImage? = null
var currentGraphics: Graphics2D? = null