package com.tlannigan.tavern.models

import com.mongodb.client.result.UpdateResult
import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.repositories.PlayerRepository
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getWorld
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.litote.kmongo.Id
import java.util.*

@Serializable
data class TPlayer(

    @Contextual
    @SerialName("_id")
    val id: UUID,

    var state: PlayerState,

    var activeCampaign: @Contextual Id<TCampaign>? = null,

    val campaigns: MutableList<@Contextual Id<TCampaign>> = mutableListOf(),

    val characters: MutableList<@Contextual Id<TCharacter>> = mutableListOf()

) {

    fun getActiveCampaign(): TCampaign? {
        return activeCampaign?.let { CampaignRepository().find(it) }
    }

    fun getAllCampaigns(): MutableList<TCampaign> {
        return CampaignRepository().findMany(campaigns)
    }

    fun getCampaignsWhereGameMaster(): MutableList<TCampaign> {
        return CampaignRepository().findManyWhereGameMaster(campaigns, id)
    }

    fun hasActiveCampaign(): Boolean {
        return activeCampaign != null
    }

    fun hasCampaigns(): Boolean {
        return campaigns.isNotEmpty()
    }

    fun getBukkitPlayer(): Player? {
        return Bukkit.getPlayer(id)
    }

    fun update(): UpdateResult {
        return PlayerRepository().update(this)
    }

}

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

    val yaw: Float,

    val pitch: Float

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
            this.yaw,
            this.pitch
        )
    }
}
