package com.tlannigan.tavern.repositories

import com.mongodb.client.MongoDatabase
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import com.tlannigan.tavern.models.TCampaign
import com.tlannigan.tavern.utils.DatabaseManager
import org.litote.kmongo.*

class CampaignRepository(db: MongoDatabase = DatabaseManager.db) {

    private val campaigns = db.getCollection<TCampaign>("campaigns")

    fun create(campaign: TCampaign): InsertOneResult {
        return campaigns.insertOne(campaign)
    }

    fun find(campaignId: Id<TCampaign>): TCampaign? {
        return campaigns.findOne(TCampaign::id eq campaignId)
    }

    fun findMany(): MutableList<TCampaign> {
        return campaigns.find(TCampaign::playerLimit eq 8).toMutableList()
    }

    fun update(campaign: TCampaign): UpdateResult {
        return campaigns.updateOne(TCampaign::id eq campaign.id)
    }

    fun delete(campaign: TCampaign): DeleteResult {
        return campaigns.deleteOne(TCampaign::id eq campaign.id)
    }

}
