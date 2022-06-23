package com.tlannigan.tavern.repositories

import com.mongodb.client.MongoDatabase
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import com.tlannigan.tavern.models.TPlayer
import com.tlannigan.tavern.utils.DatabaseManager
import org.bukkit.entity.Player
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.updateOne
import java.util.*

class PlayerRepository(db: MongoDatabase = DatabaseManager.db) {

    private val players = db.getCollection<TPlayer>("players")

    fun create(tPlayer: TPlayer): InsertOneResult {
        return players.insertOne(tPlayer)
    }

    fun find(player: Player): TPlayer? {
        return players.findOne(TPlayer::id eq player.uniqueId)
    }

    fun find(id: UUID): TPlayer? {
        return players.findOne(TPlayer::id eq id)
    }

    fun update(tPlayer: TPlayer): UpdateResult {
        return players.updateOne(tPlayer)
    }

    fun delete(player: Player): DeleteResult {
        return players.deleteOne(TPlayer::id eq player.uniqueId)
    }

}
