package com.tlannigan.tavern.repositories

import com.mongodb.client.MongoDatabase
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import com.tlannigan.tavern.models.TCampaign
import com.tlannigan.tavern.models.TCharacter
import com.tlannigan.tavern.utils.DatabaseManager
import org.bson.conversions.Bson
import org.litote.kmongo.*
import java.util.*

class CampaignRepository(db: MongoDatabase = DatabaseManager.db) {

    private val campaigns = db.getCollection<TCampaign>("campaigns")

    fun create(campaign: TCampaign): InsertOneResult {
        return campaigns.insertOne(campaign)
    }

    fun find(campaignId: Id<TCampaign>): TCampaign? {
        return campaigns.findOne(TCampaign::id eq campaignId)
    }

    fun findMany(ids: MutableList<Id<TCampaign>>): MutableList<TCampaign> {
        val filterList: MutableList<Bson> = mutableListOf()
        for (id: Id<TCampaign> in ids) {
            filterList.add(TCampaign::id eq id)
        }

        return campaigns.find(or(filterList)).toMutableList()
    }

    fun findManyInSession(ids: MutableList<Id<TCampaign>>): MutableList<TCampaign> {
        val filterList: MutableList<Bson> = mutableListOf()
        for (id: Id<TCampaign> in ids) {
            filterList.add(TCampaign::id eq id)
        }

        return campaigns.find(or(filterList), TCampaign::inSession eq true).toMutableList()
    }

    fun findManyWhereGameMaster(ids: MutableList<Id<TCampaign>>, uuid: UUID): MutableList<TCampaign> {
        val filterList: MutableList<Bson> = mutableListOf()
        for (id: Id<TCampaign> in ids) {
            filterList.add(TCampaign::id eq id)
        }

        return campaigns.find(or(filterList), TCampaign::gameMaster / TCharacter::uuid eq uuid).toMutableList()
    }

    fun update(campaign: TCampaign): UpdateResult {
        return campaigns.updateOne(campaign)
    }

    fun delete(campaign: TCampaign): DeleteResult {
        return campaigns.deleteOne(TCampaign::id eq campaign.id)
    }

}
