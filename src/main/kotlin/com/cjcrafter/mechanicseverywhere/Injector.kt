package com.cjcrafter.mechanicseverywhere

import me.deecaad.core.file.SerializeData
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

        try {
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

                    val stream = Files.newInputStream(file)
                    val config: YamlConfiguration

                    try {
                        config = YamlConfiguration()
                        config.load(InputStreamReader(stream))
                    } catch (ex: InvalidConfigurationException) {
                        MechanicsEverywhere.plugin.debug.log(
                            LogLevel.WARN,
                            "Could not read file '${file.toFile()}'... the yaml format seems to be wrong!"
                        )
                        return FileVisitResult.CONTINUE
                    }

                    // TODO check to see if we are in a special folder
                    for (key in config.getKeys(false)) {

                        // Files in the commands folder should be serial
                        if (folder == "commands") {

                        }
                    }

                    return FileVisitResult.CONTINUE
                }
            })
        } catch (var3: Throwable) {
            throw InternalError(var3)
        }
    }
}