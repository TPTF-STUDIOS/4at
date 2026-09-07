@file:Suppress("UnusedVariable")

package xyz.paintingthefish.chat

import org.ini4j.Wini
import xyz.paintingthefish.chat.internals.ServerClass
import xyz.paintingthefish.chat.internals.Shared
import java.io.File
import java.util.*
import kotlin.system.exitProcess

/**
 * 
 * @author To Paint The Fish Studios™
 * @version 0.0.0
 * @see Client
 */
@Suppress("unused")
object Server {
    private val serverInstance: ServerClass? = null

    @JvmStatic
    fun main(args: Array<String>) {
        val input = Scanner(System.`in`)
        val os = System.getProperty("os.name").lowercase(Locale.getDefault())
        System.out.printf("ЧATV${Shared.getVersion()} Server Software\nproduct of To Paint The Fish Studios™\n%s\n", os)

        if (!os.contains("nux")) {
            System.err.println("[ERROR] ЧAT Server software only runs on linux")
            exitProcess(1)
        }


        if ((!File(System.getProperty("user.home") + "/.чат/server/config.ini").exists()) || Shared.hasFlag(
                args,
                "--setup"
            )
        ) {
            val cfg: Wini =
                Shared.getIniFromFpath(System.getProperty("user.home") + "/.чат/server/config.ini")
            println("Welcome to the ЧAT provider setup wizard!")
            var defaultValue: Any = Objects.requireNonNullElse(cfg.get("info", "name"), "ЧATProvider")
            System.out.printf(
                "What would you like the provider to be named? (may be changed by clients you authorize)? (%s)\n>> ",
                defaultValue
            )
            var nl: String = input.nextLine()
            cfg.put(
                "info",
                "name",
                if (nl != "") nl else defaultValue
            )
            defaultValue = Objects.requireNonNullElse(
                cfg.get("info", "name"),
                System.getProperty("user.home") + "/.чат/server/conf.db"
            )
            System.out.printf(
                "Where would you like to put the SQLite database containing info? (%s)\n>> ",
                Objects.requireNonNullElse(
                    cfg.get("info", "port"),
                    System.getProperty("user.home") + "/.чат/server/conf.db"
                )
            )
            nl = input.nextLine()
            cfg.put(
                "info",
                "db",
                if (nl != "") nl else defaultValue
            )
            // "jdbc:sqlite:${cfg.get("info", "db")}"
            val server = ServerClass(cfg)
        }
        val cfg: Wini =
            Shared.getIniFromFpath(System.getProperty("user.home") + "/.ЧAT/server/config.ini")
    }
}
