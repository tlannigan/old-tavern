package com.tlannigan.tavern.utils

import com.tlannigan.tavern.models.PlayerState
import com.tlannigan.tavern.models.TLocation
import com.tlannigan.tavern.models.TPlayer
import com.tlannigan.tavern.repositories.PlayerRepository
import org.bukkit.Location
import org.bukkit.entity.Player

fun Player.getTPlayer(): TPlayer? {
    // Try to find TPlayer in DB
    val tPlayer = PlayerRepository().find(this)
    if (tPlayer != null) {
        return tPlayer
    } else {
        // Create a new TPlayer
        val player = this.toTPlayer()
        val savedPlayer = PlayerRepository().create(player)

        return if (savedPlayer.insertedId != null) {
            player
        } else {
            null
        }
    }
}

fun Player.toTPlayer(): TPlayer {
    return TPlayer(
        this.uniqueId,
        this.getPlayerState()
    )
}

fun Player.getPlayerState(): PlayerState {
    return PlayerState(
        this.health,
        this.foodLevel,
        this.location.toTLocation(),
        null
    )
}

fun Player.applyState(state: PlayerState) {
    this.health = state.health
    this.foodLevel = state.mana
    this.teleport(state.location.toLocation())
    // TODO("Apply inventory in PlayerState")
}

/**
 * Converts Bukkit Location into serializable location
 */
fun Location.toTLocation(): TLocation {
    return TLocation(
        this.world.name,
        this.x,
        this.y,
        this.z,
        this.yaw,
        this.pitch
    )
}