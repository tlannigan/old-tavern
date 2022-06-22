package com.tlannigan.tavern.models

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit.getWorld
import org.bukkit.Location
import org.bukkit.inventory.Inventory
import java.util.UUID
import kotlin.reflect.full.starProjectedType

@Serializable
data class TPlayer(

    @SerialName("_id")
    @Contextual
    val id: UUID,

    val state: PlayerState,

//    val activeCampaign: Id<TCampaign>? = null,
//
//    val campaigns: Set<TCampaign>? = emptySet(),
//
//    val characters: Set<TCharacter>? = emptySet()

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
