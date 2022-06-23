package com.tlannigan.tavern.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class TCharacter(

    val uuid: @Contextual UUID,

    var name: String,

    var state: PlayerState

)