package com.tlannigan.tavern.models

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.repositories.PlayerRepository
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class TCampaign(

    @Contextual
    @SerialName("_id")
    val id: Id<TCampaign> = newId(),

    var gameMaster: TCharacter,

    var spawn: TLocation,

    var name: String,

    var isPublic: Boolean = false,

    var inSession: Boolean = false,

    var sessionState: SessionState = SessionState.FREE,

    var playerLimit: Int = 8,

    val characters: MutableList<TCharacter> = mutableListOf()

) {

    fun getAllPlayers(): MutableList<TPlayer> {
        val tPlayerIDs = characters.map { it.uuid }.toMutableList()
        return PlayerRepository().findMany(tPlayerIDs)
    }

    fun getPlayersInSession(): MutableList<TPlayer> {
        val charactersInSession = characters.filter { it.inSession }
        val tPlayerIds = charactersInSession.map { it.uuid }.toMutableList()
        return PlayerRepository().findMany(tPlayerIds)
    }

    fun update(): UpdateResult {
        return CampaignRepository().update(this)
    }

    fun delete(): DeleteResult {
        return CampaignRepository().delete(this)
    }

}

enum class SessionState { FREE, COMBAT, FROZEN }
