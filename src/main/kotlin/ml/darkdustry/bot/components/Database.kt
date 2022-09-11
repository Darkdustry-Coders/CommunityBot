package ml.darkdustry.bot.components

import ml.darkdustry.bot.Vars
import org.iq80.leveldb.DB
import org.iq80.leveldb.impl.Iq80DBFactory
import java.io.File

object Database {
    fun write(database: Databases, key: ByteArray, value: ByteArray) {
        val db: DB = Iq80DBFactory.factory.open(
            File("${Vars.mainDir.absolutePath()}/${database.name}"),
            Vars.options
        )

        db.use {
            db.put(key, value)
        }
    }

    fun get(database: Databases, key: ByteArray): ByteArray {
        val db: DB = Iq80DBFactory.factory.open(
            File("${Vars.mainDir.absolutePath()}/${database.name}"),
            Vars.options
        )

        db.use {
            return db.get(key)
        }
    }

    fun delete(database: Databases, key: ByteArray) {
        val db: DB = Iq80DBFactory.factory.open(
            File("${Vars.mainDir.absolutePath()}/${database.name}"),
            Vars.options
        )

        db.use {
            db.delete(key)
        }
    }

    fun rewrite(database: Databases, key: ByteArray, value: ByteArray) {
        val db: DB = Iq80DBFactory.factory.open(
            File("${Vars.mainDir.absolutePath()}/${database.name}"),
            Vars.options
        )

        db.use {
            db.delete(key).apply {
                db.put(key, value)
            }
        }
    }

    enum class Databases {
        Suggestions, Warns, Mutes, Appeals
    }
}