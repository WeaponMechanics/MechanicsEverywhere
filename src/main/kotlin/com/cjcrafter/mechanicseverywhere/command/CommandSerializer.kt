package com.cjcrafter.mechanicseverywhere.command

import me.deecaad.core.commands.CommandBuilder
import me.deecaad.core.commands.HelpCommandBuilder
import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.Serializer
import me.deecaad.core.lib.adventure.text.minimessage.MiniMessage
import me.deecaad.core.mechanics.CastData
import me.deecaad.core.mechanics.Mechanics
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault

class CommandSerializer : Serializer<CommandSerializer> {

    lateinit var command: CommandBuilder
    var mechanics: Mechanics? = null
    var subcommands: List<CommandSerializer> = emptyList()

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(command: CommandBuilder, mechanics: Mechanics?, subcommands: List<CommandSerializer>) {
        this.command = command
        this.mechanics = mechanics
        this.subcommands = subcommands
    }

    override fun serialize(data: SerializeData): CommandSerializer {
        return serialize(data, false)
    }

    /**
     * We have to separate this method since we should not be registering subcommands.
     * Only the main command can be registered, else we will flood the command list.
     */
    private fun serialize(data: SerializeData, isSubcommand: Boolean): CommandSerializer {
        val label = data.key.split("\\.").last()

        var mechanics: Mechanics? = null
        val subcommands: MutableList<CommandSerializer> = mutableListOf()

        val command = command(label) {

            // Should have one of 'Mechanics' or 'Subcommands'
            if (!data.has("Mechanics") && !data.has("Subcommands")) {
                throw data.exception(null, "Custom commands should have either Mechanics or Subcommands",
                    "You have neither on your command... Please add 1")
            }

            // Optional command things
            if (data.has("Description"))
                description = data.of("Description").assertExists().assertType(String::class.java).get()
            if (data.has("Permission"))
                permission = serializePermission(data.move("Permission"))
            if (data.has("Aliases"))
                aliases = data.of("Aliases").assertExists().assertType(List::class.java).get<List<Any>>().map { it.toString() } // make sure it is strings

            // The mechanics are the actions run when this command is executed
            mechanics = data.of("Mechanics").serialize(Mechanics::class.java)
            if (mechanics != null) {
                executeLivingEntity { sender, args ->
                    val cast = CastData(sender, null, null)
                    mechanics!!.use(cast)
                }
            }
            
            val subcommandSection: ConfigurationSection =
                data.of("Subcommands").assertExists().assertType(ConfigurationSection::class.java).get()
            if (subcommandSection.getKeys(false).isEmpty()) {
                throw data.exception(
                    null, "Found the 'Subcommands' section in the command, but it was empty!",
                    "Check the wiki for more information on how to add custom commands."
                )
            }

            for (key in subcommandSection.getKeys(false)) {
                // Just an assertion, has no functionality
                data.of("Subcommands.$key").assertType(ConfigurationSection::class.java)

                // Recursively create the command branch, then add it to this command as a subcommand.
                // This allows subcommands to have subcommands.
                val recursive = data.move("Subcommands.$key")
                val subcommand = serialize(recursive, true) // recursive call to create subcommand branch
                subcommands.add(subcommand)
                this@command.subcommands.add(subcommand.command)
            }
        }

        // When this command is NOT a subcommand, then we should try to
        // register the help command, and register the main command.
        if (!isSubcommand) {
            if (data.has("Help")) {

                // Minimessage doesn't directly support parsing a Style instance
                // from a string, so we have to parse a component and extract the style
                val mini = MiniMessage.miniMessage();
                val color = mini.deserialize(data.of("Color").assertExists().adventure + "dummy").style()
                val accent = mini.deserialize(data.of("Accent").assertExists().adventure + "dummy").style()
                val symbol = data.of("Symbol").assertType(String::class.java).get("➢")

                command.registerHelp(HelpCommandBuilder.HelpColor(color, accent, symbol))
            }

            // Register the main command
            command.register()
        }

        // Just an assertion, has no functionality... subcommands should not use colors
        else if (data.has("Help")) {
            throw data.exception("Help", "Cannot use 'Help' on subcommands, that has to be on the main command")
        }

        // If this is a main command (not a subcommand), this value is discarded
        // since the command has already been registered above^
        return CommandSerializer(command, mechanics, subcommands)
    }

    private fun serializePermission(data: SerializeData): Permission {
        val name: String = data.of("Name").assertExists().assertType(String::class.java).get()
        val description: String = data.of("Description").get("Permission to use $name")
        val default: PermissionDefault = data.of("Default").getEnum(PermissionDefault::class.java, PermissionDefault.OP)

        return Permission(name, description, default)
    }
}