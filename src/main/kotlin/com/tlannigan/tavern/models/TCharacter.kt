package com.tlannigan.tavern.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.util.*

@Serializable
data class TCharacter(

    @Contextual
    @SerialName("_id")
    val id: Id<TCharacter> = newId(),

    val uuid: @Contextual UUID,

    var name: String,

    val state: PlayerState

)