package com.tlannigan.tavern.listeners

import com.tlannigan.tavern.repositories.PlayerRepository
import com.tlannigan.tavern.utils.toTPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val playerRepo = PlayerRepository()
        val playerExists = playerRepo.find(event.player.uniqueId)
        if (playerExists == null) {
            PlayerRepository().create(event.player.toTPlayer())
        }
    }

}