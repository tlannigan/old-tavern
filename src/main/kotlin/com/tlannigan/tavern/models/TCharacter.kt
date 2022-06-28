package com.tlannigan.tavern.models

import com.tlannigan.tavern.repositories.PlayerRepository
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class TCharacter(

    val uuid: @Contextual UUID,

    var name: String,

    var state: PlayerState,

    var inSession: Boolean

) {

    fun getTPlayer(): TPlayer? {
        return PlayerRepository().find(uuid)
    }

}
