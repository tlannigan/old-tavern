package com.tlannigan.tavern.utils

import com.tlannigan.tavern.models.PlayerState
import com.tlannigan.tavern.models.TCharacter
import com.tlannigan.tavern.models.TLocation
import com.tlannigan.tavern.models.TPlayer
import com.tlannigan.tavern.repositories.PlayerRepository
import org.bukkit.Location
import org.bukkit.entity.Player

fun Player.getTPlayer(): TPlayer? {
    // Try to find TPlayer in DB
    val tPlayer = PlayerRepository().find(this)
    return if (tPlayer != null) {
        tPlayer
    } else {
        // Create a new TPlayer
        val player = this.toTPlayer()
        val savedPlayer = PlayerRepository().create(player)

        if (savedPlayer.insertedId != null) {
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

fun Player.buildCharacter(): TCharacter {
    return TCharacter(
        uuid = this.uniqueId,
        name = this.name,
        state = this.getPlayerState(),
        inSession = false
    )
}

fun Player.buildCharacter(tLocation: TLocation): TCharacter {
    return TCharacter(
        uuid = this.uniqueId,
        name = this.name,
        state = PlayerState(
            health = this.health,
            mana = this.foodLevel,
            location = tLocation
        ),
        inSession = false
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