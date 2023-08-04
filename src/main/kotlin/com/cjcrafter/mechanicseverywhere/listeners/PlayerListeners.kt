package com.cjcrafter.mechanicseverywhere.listeners

import me.deecaad.core.mechanics.CastData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerListeners : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val cast = CastData(event.player, null, null)

    }
}