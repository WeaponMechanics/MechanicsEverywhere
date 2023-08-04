package com.cjcrafter.mechanicseverywhere

import com.cjcrafter.mechanicseverywhere.command.CommandSerializer
import me.deecaad.core.file.BukkitConfig
import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.SerializerException
import me.deecaad.core.mechanics.Mechanics
import me.deecaad.core.utils.FileUtil.PathReference
import me.deecaad.core.utils.LogLevel
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

object Injector {

    fun inject() {
        val searchFolder = MechanicsEverywhere.plugin.dataFolder

        val pathReference = PathReference.of(searchFolder.toURI())
        Files.walkFileTree(pathReference.path, object : SimpleFileVisitor<Path>() {

            var folder: String? = null

            override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
                folder = dir.fileName.toString()
                return FileVisitResult.CONTINUE
            }

            override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
                folder = null
                return FileVisitResult.CONTINUE
            }

            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                // Only parse valid YAML files
                if (!file.toString().endsWith(".yml", ignoreCase = true) && !file.toString().endsWith(".yaml", ignoreCase = true))
                    return FileVisitResult.CONTINUE
                if (file.fileName.startsWith("#"))
                    return FileVisitResult.CONTINUE

                val stream = Files.newInputStream(file)
                val config: YamlConfiguration

                try {
                    config = YamlConfiguration()
                    config.load(InputStreamReader(stream))

                    for (key in config.getKeys(false)) {


                        // Files in the commands folder are not just mechanics, and we
                        // must handle them separately
                        if (folder == "commands") {
                            val data = SerializeData("CustomCommand", file.toFile(), key, BukkitConfig(config))
                            data.of().serialize(CommandSerializer::class.java)
                            continue
                        }

                        val data = SerializeData("Mechanics", file.toFile(), key, BukkitConfig(config))
                        val mechanics = data.of().serialize(Mechanics::class.java)


                        when (folder) {
                            // Pools are groups of mechanics that can be called (as a mechanic)
                            "mechanics_pools" -> {
                                MechanicsEverywhere.plugin.mechanicPools[key] = mechanics
                            }

                            // Repeated mechanics repeat every x ticks
                            "repeated_mechanics" -> {
                                val delayBetweenMechanics = key.toIntOrNull()
                                if (delayBetweenMechanics == null || delayBetweenMechanics < 1) {
                                    throw data.exception(null, "Failed to find a positive number for the ticks between mechanics",
                                        "Found '$key' but expected to get a positive integer. Remember that since you are in the 'repeated_mechanics'",
                                        "folder that each name in config should be a number representing the how many ticks later should the mechanics be repeated.")
                                }

                                MechanicsEverywhere.plugin.repeatedMechanics[delayBetweenMechanics]
                            }

                            // Normal mechanics played on events... add to config map
                            else -> {
                                MechanicsEverywhere.plugin.config.set(key, mechanics)
                            }
                        }
                    }
                } catch (ex: InvalidConfigurationException) {
                    MechanicsEverywhere.plugin.debug.log(
                        LogLevel.WARN,
                        "Could not read file '${file.toFile()}'... the yaml format is wrong!"
                    )
                } catch (ex: SerializerException) {
                    ex.log(MechanicsEverywhere.plugin.debug)
                }

                return FileVisitResult.CONTINUE
            }
        })
    }
}