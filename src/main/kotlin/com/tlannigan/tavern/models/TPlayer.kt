package com.tlannigan.tavern.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit.getWorld
import org.bukkit.Location
import org.bukkit.inventory.Inventory
import org.litote.kmongo.Id
import java.util.*

@Serializable
data class TPlayer(

    @Contextual
    @SerialName("_id")
    val id: UUID,

    val state: PlayerState,

    val activeCampaign: Id<TCampaign>? = null,

    val campaigns: MutableList<TCampaign> = mutableListOf(),

    val characters: MutableList<TCharacter> = mutableListOf()

)

@Serializable
data class PlayerState(

    val health: Double,

    val mana: Int,

    val location: TLocation,

    val inventory: Inventory? = null

)

@Serializable
data class TLocation(

    val world: String,

    val x: Double,

    val y: Double,

    val z: Double,

    val pitch: Float,

    val yaw: Float

) {

    /**
     * Converts serialized location into Bukkit Location
     */
    fun toLocation(): Location {
        return Location(
            getWorld(world),
            this.x,
            this.y,
            this.z,
            this.pitch,
            this.yaw
        )
    }
}
