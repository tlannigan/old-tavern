package com.tlannigan.tavern.models

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId

data class TCampaign(

    @BsonId
    val id: Id<TCampaign> = newId(),

    val name: String,

    val isPublic: Boolean = false,

    val inSession: Boolean = false,

    val sessionState: SessionState = SessionState.FREE,

    val playerLimit: Int = 8,

    val players: Set<TCampaignCharacter>

)

data class TCampaignCharacter(

    var character: TCharacter,

    var state: PlayerState,

    var isGameMaster: Boolean

)

enum class SessionState { FREE, COMBAT, FROZEN }
