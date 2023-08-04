package com.cjcrafter.mechanicseverywhere.listeners

import com.cjcrafter.mechanicseverywhere.MechanicsEverywhere
import me.deecaad.core.mechanics.CastData
import me.deecaad.core.mechanics.Mechanics
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Listener

interface MechanicsListener : Listener {

    fun cast(key: String, source: LivingEntity) {
        val mechanics = MechanicsEverywhere.plugin.config.getObject(key, Mechanics::class.java) ?: return
        val cast = CastData(source, null, null)
        mechanics.use(cast)
    }

    fun cast(key: String, source: LivingEntity, target: LivingEntity) {
        val mechanics = MechanicsEverywhere.plugin.config.getObject(key, Mechanics::class.java) ?: return
        val cast = CastData(source, null, null)
        cast.setTargetEntity(target)
        mechanics.use(cast)
    }

    fun cast(key: String, source: LivingEntity, target: Location) {
        val mechanics = MechanicsEverywhere.plugin.config.getObject(key, Mechanics::class.java) ?: return
        val cast = CastData(source, null, null)
        cast.setTargetLocation(target)
        mechanics.use(cast)
    }

    fun cast(key: String, source: LivingEntity, target: Block) {
        cast(key, source, target.location.add(0.5, 0.5, 0.5)) // target block center
    }
}