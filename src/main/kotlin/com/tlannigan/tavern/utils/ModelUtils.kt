package com.tlannigan.tavern.utils

import com.tlannigan.tavern.models.PlayerState
import com.tlannigan.tavern.models.TLocation
import com.tlannigan.tavern.models.TPlayer
import com.tlannigan.tavern.repositories.PlayerRepository
import org.bukkit.Location
import org.bukkit.entity.Player

fun Player.getTPlayer(): TPlayer? {
    return PlayerRepository().find(this)
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

/**
 * Converts Bukkit Location into serializable location
 */
fun Location.toTLocation(): TLocation {
    return TLocation(
        this.world.name,
        this.x,
        this.y,
        this.z,
        this.pitch,
        this.yaw
    )
}