@file:Suppress("unused")

package xyz.paintingthefish.chat.internals

import org.ini4j.Wini
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.StringReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

/**
 * @author To Paint The Fish Studios™
 * @version 1.0
 */
class Shared {

    enum class CliArgStyles {
        EQUAL_SIGN,
        SEPARATE
    }

    companion object {
        const val DEFAULT_PORT: Int = 1997

        fun getBitFrom64Bitflag(bitflag: Long, position: Int): Int {
            // 1. Shift the 64-bit flag right by the target position
            // 2. Mask it with 1L to isolate that specific bit
            val extracted = (bitflag shr position) and 1L


            // Cast the final 0 or 1 result back to a standard integer
            return extracted.toInt()
        }

        fun getVersion(): String {
            return Shared::class.java.`package`.implementationVersion ?: "???"
        }
        fun hasFlag(args: Array<String>, rawFlag: String, caseSensitive: Boolean): Boolean {
            var flag = rawFlag
            if (!caseSensitive) {
                flag = flag.lowercase(Locale.getDefault())
            }
            for (rawArg in args) {
                var arg = rawArg
                if (!caseSensitive) {
                    arg = rawArg.lowercase(Locale.getDefault())
                }

                if (arg == flag) {
                    return true
                }
            }
            return false
        }

        fun hasFlag(args: Array<String>, flag: String?): Boolean {
            for (arg in args) {
                if (arg == flag) {
                    return true
                }
            }
            return false
        }

        fun getValue(args: Array<String>, key: String, argStyle: CliArgStyles): String? {
            var i = 0

            when (argStyle) {
                CliArgStyles.SEPARATE -> {
                    for (arg in args) {
                        if (arg == key) {
                            try {
                                return args[i + 1]
                            } catch (e: ArrayIndexOutOfBoundsException) {
                                println("[ERROR] Fuck you.")
                                throw e
                            }
                        }
                        i++
                    }
                }
                CliArgStyles.EQUAL_SIGN -> {
                    for (arg in args) {
                        if (arg.startsWith("$key=")) {
                            return arg.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                        }
                        i++
                    }
                }
            }
            return null
        }

        fun getIniFromFpath(path: Path): Wini {
            val f = path.toFile()

            try {
                FileInputStream(f).use { `is` ->
                    return Wini(`is`)
                }
            } catch (e: FileNotFoundException) {
                println("[INFO] Config file not found at: $path. Creating blank INI configuration.")
                e.printStackTrace()
                val ini = Wini()
                try {
                    // 1. Get the parent folder path
                    val parentDir = path.parent

                    // 2. Create the missing folders if they don't exist
                    if (parentDir != null) {
                        Files.createDirectories(parentDir)
                    }

                    // 3. Create the empty file
                    Files.createFile(path)

                    // 4. Save the blank INI structure to the file
                    ini.store(f)

                    // FIX: Return the active ini object instead of an empty dummy
                    return ini
                } catch (err: IOException) {
                    println("[ERROR] Could not create empty config file: " + err.message)
                    return Wini()
                }
            } catch (e: IOException) {
                println("[ERROR] Invalid file type or read failure: " + e.message)
                return Wini()
            }
        }

        fun getIniFromFpath(fpath: String): Wini {
            val path = Paths.get(fpath)
            val f = path.toFile()

            try {
                FileInputStream(f).use { `is` ->
                    return Wini(`is`)
                }
            } catch (e: FileNotFoundException) {
                println("[INFO] Config file not found at: $path. Creating blank INI configuration.")
                e.printStackTrace()
                val ini = Wini()
                try {
                    // 1. Get the parent folder path
                    val parentDir = path.parent

                    // 2. Create the missing folders if they don't exist
                    if (parentDir != null) {
                        Files.createDirectories(parentDir)
                    }

                    // 3. Create the empty file
                    Files.createFile(path)

                    // 4. Save the blank INI structure to the file
                    ini.store(f)

                    // FIX: Return the active ini object instead of an empty dummy
                    return ini
                } catch (e: IOException) {
                    println("[ERROR] Could not create empty config file: " + e.message)
                    e.printStackTrace()
                    return Wini()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                println("[ERROR] Invalid file type or read failure: " + e.message)
                return Wini()
            }
        }

        fun getWiniFromStr(str: String): Wini {
            try {
                val wini = Wini()
                wini.load(StringReader(str))
                return wini
            } catch (e: Exception) {
                System.err.println("[ERROR] $e")
                return Wini()
            }
        }
    }
}
