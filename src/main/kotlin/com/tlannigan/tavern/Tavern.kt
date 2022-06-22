package com.tlannigan.tavern

import com.tlannigan.tavern.listeners.PlayerListener
import com.tlannigan.tavern.utils.ConfigManager
import com.tlannigan.tavern.utils.DatabaseManager
import org.bukkit.plugin.java.JavaPlugin

class Tavern : JavaPlugin() {

    private val config = ConfigManager(this)
    private val db = DatabaseManager(this)

    override fun onEnable() {
        config.initialize()
        db.initialize()

        registerEvents()
    }

    override fun onDisable() {
        DatabaseManager.mongoClient.close()
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(PlayerListener(), this)
    }

}
