package com.tlannigan.tavern.models

import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.repositories.PlayerRepository
import com.tlannigan.tavern.utils.getTPlayer
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
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

    fun kickPlayer(characterName: String): Boolean {
        val matchedCharacters = characters.count { it.name == characterName }

        if (matchedCharacters == 1) {
            val matchedCharacter = characters.find { it.name == characterName }

            val tPlayer = Bukkit.getPlayer(matchedCharacter!!.uuid)?.getTPlayer()

            if (tPlayer != null) {
                tPlayer.leaveCampaign()
                tPlayer.campaigns.removeAll { it == this.id }
                PlayerRepository().update(tPlayer)

                characters.removeAll { it.name == characterName }
                CampaignRepository().update(this)

                return true
            }
        }

        return false
    }

}

enum class SessionState { FREE, COMBAT, FROZEN }
