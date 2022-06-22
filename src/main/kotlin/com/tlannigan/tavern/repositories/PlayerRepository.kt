package com.tlannigan.tavern.repositories

import com.mongodb.client.MongoDatabase
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import com.tlannigan.tavern.models.PlayerState
import com.tlannigan.tavern.models.TLocation
import com.tlannigan.tavern.models.TPlayer
import com.tlannigan.tavern.utils.DatabaseManager
import org.bukkit.Location
import org.bukkit.entity.Player
import org.litote.kmongo.*

class PlayerRepository(db: MongoDatabase = DatabaseManager.db) {

    private val players = db.getCollection<TPlayer>("players")

    fun create(tPlayer: TPlayer): InsertOneResult {
        return players.insertOne(tPlayer)
    }

    fun find(player: Player): TPlayer? {
        return players.findOne(TPlayer::id eq player.uniqueId)
    }

    fun update(tPlayer: TPlayer): UpdateResult {
        return players.updateOne(TPlayer::id eq tPlayer.id)
    }

    fun delete(player: Player): DeleteResult {
        return players.deleteOne(TPlayer::id eq player.uniqueId)
    }

}

fun Player.toTPlayer(): TPlayer {
    return TPlayer(
        this.uniqueId,
        this.getPlayerState()
    )
}

fun Player.getPlayerState(): PlayerState {
    return PlayerState(
        this.health,
        this.foodLevel,
        this.location.toTLocation(),
        null
    )
}

/**
 * Converts Bukkit Location into serializable location
 */
fun Location.toTLocation(): TLocation {
    return TLocation(
        this.world.name,
        this.x,
        this.y,
        this.z,
        this.pitch,
        this.yaw
    )
}