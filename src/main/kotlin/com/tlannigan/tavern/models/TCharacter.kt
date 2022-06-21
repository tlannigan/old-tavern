package com.tlannigan.tavern.models

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.util.*

data class TCharacter(

    @BsonId
    val id: Id<TCharacter> = newId(),

    val uuid: UUID,

    var name: String

)