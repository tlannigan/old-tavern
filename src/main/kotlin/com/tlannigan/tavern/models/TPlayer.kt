package com.tlannigan.tavern.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bukkit.inventory.Inventory
import org.litote.kmongo.Id
import org.litote.kmongo.newId

data class TPlayer(

    @BsonId
    val id: Id<TPlayer> = newId(),

    val state: PlayerState,

    val activeCampaign: Id<TCampaign>,

    val campaigns: Set<TCampaign>,

    val characters: Set<TCharacter>

)

data class PlayerState(

    val health: Double,

    val mana: Int,

    val location: Map<String, Any>,

    val inventory: Inventory? = null

)