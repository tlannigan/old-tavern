package com.tlannigan.tavern.utils

import org.bukkit.plugin.java.JavaPlugin

class ConfigManager(private val plugin: JavaPlugin) {

    fun initialize() {
        plugin.config.options().copyDefaults(true)
        plugin.saveDefaultConfig()
    }

    fun getDBConfig(): DBConfig {
        val hostname = plugin.config.getString("database.hostname")
        val user = plugin.config.getString("database.user")
        val password = plugin.config.getString("database.password")
        val databaseName = plugin.config.getString("database.name")

        return DBConfig(hostname, user, password, databaseName)
    }

}

data class DBConfig(
    val hostname: String?,
    val user: String?,
    val password: String?,
    val databaseName: String?
)