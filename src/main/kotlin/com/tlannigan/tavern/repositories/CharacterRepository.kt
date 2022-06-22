package com.tlannigan.tavern.repositories

import com.mongodb.client.MongoDatabase
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import com.tlannigan.tavern.models.TCharacter
import com.tlannigan.tavern.utils.DatabaseManager
import org.litote.kmongo.*

class CharacterRepository(db: MongoDatabase = DatabaseManager.db) {

    private val characters = db.getCollection<TCharacter>("characters")

    fun create(character: TCharacter): InsertOneResult {
        return characters.insertOne(character)
    }

    fun find(characterId: Id<TCharacter>): TCharacter? {
        return characters.findOne(TCharacter::id eq characterId)
    }

    fun update(character: TCharacter): UpdateResult {
        return characters.updateOne(TCharacter::id eq character.id)
    }

    fun delete(character: TCharacter): DeleteResult {
        return characters.deleteOne(TCharacter::id eq character.id)
    }

}