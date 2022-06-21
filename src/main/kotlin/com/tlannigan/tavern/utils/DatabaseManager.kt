package com.tlannigan.tavern.utils

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.bukkit.plugin.java.JavaPlugin
import org.litote.kmongo.KMongo

class DatabaseManager(private val plugin: JavaPlugin) {

    fun initialize() {
        val config = ConfigManager(plugin)
        val (hostname, user, password, databaseName) = config.getDBConfig()

        val connectionString =
            ConnectionString("mongodb+srv://${user}:${password}@${hostname}/${databaseName}?retryWrites=true&w=majority")

        val settings: MongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build()

        mongoClient = KMongo.createClient(settings)
        db = mongoClient.getDatabase("tavern")
    }

    companion object {

        lateinit var mongoClient: MongoClient
        lateinit var db: MongoDatabase

    }
    
}