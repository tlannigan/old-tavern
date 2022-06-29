package com.tlannigan.tavern

import com.tlannigan.tavern.commands.gamemaster.Delete.Companion.deleteCommand
import com.tlannigan.tavern.commands.gamemaster.End.Companion.endCommand
import com.tlannigan.tavern.commands.gamemaster.Invite.Companion.inviteCommand
import com.tlannigan.tavern.commands.gamemaster.Kick.Companion.kickCommand
import com.tlannigan.tavern.commands.gamemaster.Start.Companion.startCommand
import com.tlannigan.tavern.commands.player.Create.Companion.createCommand
import com.tlannigan.tavern.commands.player.Enter.Companion.enterCommand
import com.tlannigan.tavern.commands.player.Leave.Companion.leaveCommand
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
        createCommand.register()
        enterCommand.register()
        leaveCommand.register()

        startCommand.register()
        endCommand.register()
        inviteCommand.register()
        kickCommand.register()
        deleteCommand.register()
    }

}
