package com.tlannigan.tavern

import com.tlannigan.tavern.commands.Campaign.campaignCommand
import com.tlannigan.tavern.commands.FreezePlayer.freezePlayerCommand
import com.tlannigan.tavern.commands.GameMaster.gameMasterCommand
import com.tlannigan.tavern.commands.UnfreezePlayer.unfreezePlayerCommand
import com.tlannigan.tavern.listeners.PlayerListener
import com.tlannigan.tavern.utils.ConfigManager
import com.tlannigan.tavern.utils.DatabaseManager
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIConfig
import org.bukkit.plugin.java.JavaPlugin

class Tavern : JavaPlugin() {

    private val config = ConfigManager(this)
    private val db = DatabaseManager(this)

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIConfig().verboseOutput(true))
        registerCommands()
    }

    override fun onEnable() {
        config.initialize()
        db.initialize()

        CommandAPI.onEnable(this)

        registerEvents()
    }

    override fun onDisable() {
        db.deinitialize()
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(PlayerListener(), this)
    }

    private fun registerCommands() {
        campaignCommand.register()
        gameMasterCommand.register()
        freezePlayerCommand.register()
        unfreezePlayerCommand.register()
    }

}
