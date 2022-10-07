package tk.darkdustry.bot

import arc.files.Fi
import arc.util.serialization.Json
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel

lateinit var config: ConfigUtils.Config

lateinit var jda: JDA
lateinit var guild: Guild

lateinit var mapsChannel: TextChannel
lateinit var schematicsChannel: TextChannel

val json = Json()
val dataDirectory = Fi(".community")