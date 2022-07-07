package com.tlannigan.tavern.utils

import com.tlannigan.tavern.models.PlayerState
import com.tlannigan.tavern.models.TCharacter
import com.tlannigan.tavern.models.TLocation
import com.tlannigan.tavern.models.TPlayer
import com.tlannigan.tavern.repositories.PlayerRepository
import com.tlannigan.tavern.utils.Constants.emptyArmorContents
import com.tlannigan.tavern.utils.Constants.emptyContents
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

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
        state = PlayerState(
            health = 20.00,
            mana = 20,
            location = this.location.toTLocation(),
            inventory = arrayOf(emptyContents, emptyArmorContents)
        ),
        inSession = false,
    )
}

fun Player.buildCharacter(tLocation: TLocation): TCharacter {
    return TCharacter(
        uuid = this.uniqueId,
        name = this.name,
        state = PlayerState(
            health = 20.00,
            mana = 20,
            location = tLocation,
            inventory = arrayOf(emptyContents, emptyArmorContents)
        ),
        inSession = false
    )
}

fun Player.getPlayerState(): PlayerState {
    return PlayerState(
        this.health,
        this.foodLevel,
        this.location.toTLocation(),
        this.inventory.serialize()
    )
}

fun Player.applyState(state: PlayerState) {
    this.health = state.health
    this.foodLevel = state.mana

    this.teleport(state.location.toLocation())

    val contents = Serializer().itemStackArrayFromBase64(state.inventory[0])
    val armorContents = Serializer().itemStackArrayFromBase64(state.inventory[1])

    if (contents != null) {
        this.inventory.contents = contents
    }

    if (armorContents != null) {
        this.inventory.armorContents = armorContents
    }
}

fun Player.freeze() {
    if (!this.scoreboardTags.contains("frozen")) {
        this.addScoreboardTag("frozen")
        this.walkSpeed = 0.0F
        val jumpBoost: PotionEffect? = this.getPotionEffect(PotionEffectType.JUMP)
        if (jumpBoost == null) {
            this.addPotionEffect(
                PotionEffect(PotionEffectType.JUMP, 31536000, 128, true, true, false)
            )
        }
    }
}

fun Player.unfreeze() {
    if (this.scoreboardTags.contains("frozen")) {
        this.removeScoreboardTag("frozen")
        this.walkSpeed = 0.2F
        val jumpBoost: PotionEffect? = this.getPotionEffect(PotionEffectType.JUMP)
        if (jumpBoost != null) {
            this.removePotionEffect(PotionEffectType.JUMP)
        }
    }
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

fun PlayerInventory.serialize(): Array<String> {
    return Serializer().playerInventoryToBase64(this)
}
