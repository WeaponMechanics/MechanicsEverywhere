package com.cjcrafter.mechanicseverywhere

import com.cjcrafter.mechanicseverywhere.command.Command
import me.deecaad.core.file.Configuration
import me.deecaad.core.mechanics.Mechanics
import me.deecaad.core.utils.Debugger
import me.deecaad.core.utils.FileUtil
import org.bstats.bukkit.Metrics
import org.bstats.charts.SimpleBarChart
import org.bukkit.plugin.java.JavaPlugin

class MechanicsEverywhere : JavaPlugin() {

    lateinit var debug: Debugger
    var metrics: Metrics? = null

    // Stuff from config
    var repeatedMechanics: Map<Int, Mechanics> = HashMap()
    lateinit var config: Configuration
    var mechanicPools: Map<String, Mechanics> = HashMap()

    override fun onLoad() {
        plugin = this

        val level = getConfig().getInt("Debug_Level", 2)
        val printTraces = getConfig().getBoolean("Print_Traces", false)
        debug = Debugger(logger, level, printTraces)
    }

    override fun onEnable() {
        writeFiles()
        loadConfig()
        registerDebugger()
        registerBStats()
        registerListeners()

        Command.register()
    }

    fun writeFiles() {
        if (!dataFolder.exists() || dataFolder.listFiles() == null || dataFolder.listFiles()?.size == 0) {
            debug.info("Copying files from jar (This process may take up to 30 seconds during the first load!)")
            FileUtil.copyResourcesTo(classLoader.getResource("MechanicsEverywhere"), dataFolder.toPath())
        }
    }

    fun loadConfig() {
        Injector.inject()
    }

    fun registerDebugger() {
        debug.permission = "mechanicseverywhere.errorlog"
        debug.msg = "MechanicsEverywhere had %s error(s) in console."
        debug.start(this)
    }

    fun registerBStats() {
        if (metrics != null) return

        // See https://bstats.org/plugin/bukkit/MechanicsEverywhere/19380. This is
        // the bStats plugin id used to track information.
        val id = 19380
        metrics = Metrics(this, id)

        // When users download this from Spigot, %%__USER__%% is replaced with
        // the user's id.
        val isPremium = try {
            Integer.parseInt("%%__USER__%%")
            true
        } catch (ex: NumberFormatException) {
            false
        }

        metrics!!.addCustomChart(SimpleBarChart("premium") {
            mapOf((if (isPremium) "Premium" else "Non-Premium") to 1)
        })
    }

    fun registerListeners() {

    }


    companion object {
        lateinit var plugin: MechanicsEverywhere
    }
}
