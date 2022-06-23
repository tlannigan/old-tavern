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

    var gameMaster: TCharacter,

    var spawn: TLocation,

    var name: String,

    var isPublic: Boolean = false,

    var inSession: Boolean = false,

    var sessionState: SessionState = SessionState.FREE,

    var playerLimit: Int = 8,

    val characters: MutableList<TCharacter> = mutableListOf()

)

enum class SessionState { FREE, COMBAT, FROZEN }
