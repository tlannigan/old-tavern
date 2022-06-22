package com.tlannigan.tavern.utils

import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.bukkit.plugin.java.JavaPlugin
import org.litote.kmongo.*
import org.litote.kmongo.serialization.SerializationClassMappingTypeService

class DatabaseManager(private val plugin: JavaPlugin) {

    fun initialize() {
        val config = ConfigManager(plugin)
        val (hostname, user, password, databaseName) = config.getDBConfig()

        val connectionString =
            ConnectionString("mongodb+srv://${user}:${password}@${hostname}/${databaseName}?retryWrites=true&w=majority")

        // Required property setter for applications loaded at runtime
        System.setProperty("org.litote.mongo.mapping.service", SerializationClassMappingTypeService::class.qualifiedName!!)

        mongoClient = KMongo.createClient(connectionString)
        db = mongoClient.getDatabase("tavern")
    }

    fun deinitialize() {
        mongoClient.close()
    }

    companion object {

        lateinit var mongoClient: MongoClient
        lateinit var db: MongoDatabase

    }
    
}