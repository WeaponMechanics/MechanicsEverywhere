package com.cjcrafter.mechanicseverywhere.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerBedLeaveEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * [Wiki](https://cjcrafter.gitbook.io/mechanicseverywhere/events/player)
 */
class PlayerListeners : MechanicsListener {

    @EventHandler (priority = EventPriority.MONITOR)
    fun onPlayerJoin(event: PlayerJoinEvent) = cast("Player_Join_Event", event.player)

    @EventHandler (priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) = cast("Player_Quit_Event", event.player)

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerEnterBed(event: PlayerBedEnterEvent) = cast("Player_Bed_Enter_Event", event.player, event.bed)

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerLeaveBed(event: PlayerBedLeaveEvent) = cast("Player_Bed_Leave_Event", event.player, event.bed)

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) = cast("Block_Break_Event", event.player, event.block)

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) = cast("Block_Place_Event", event.player, event.blockPlaced)
}