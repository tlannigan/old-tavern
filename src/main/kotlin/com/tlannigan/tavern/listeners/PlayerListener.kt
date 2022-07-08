package com.tlannigan.tavern.listeners

import com.tlannigan.tavern.repositories.PlayerRepository
import com.tlannigan.tavern.utils.getTPlayer
import com.tlannigan.tavern.utils.toTPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val playerRepo = PlayerRepository()
        val playerExists = playerRepo.find(event.player.uniqueId)
        if (playerExists == null) {
            playerRepo.create(event.player.toTPlayer())
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val tPlayer = event.player.getTPlayer()
        val activeCampaign = tPlayer?.getActiveCampaign()

        if (activeCampaign != null) {
            event.player.performCommand("campaign leave")
        }
    }

}