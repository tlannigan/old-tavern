package com.tlannigan.tavern.models

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

    val gameMaster: TCharacter,

    val name: String,

    val isPublic: Boolean = false,

    val inSession: Boolean = false,

    val sessionState: SessionState = SessionState.FREE,

    val playerLimit: Int = 8,

    val players: MutableList<TCharacter> = mutableListOf()

)

enum class SessionState { FREE, COMBAT, FROZEN }
