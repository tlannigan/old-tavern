package com.tlannigan.tavern.listeners

import com.tlannigan.tavern.repositories.PlayerRepository
import com.tlannigan.tavern.repositories.toTPlayer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        PlayerRepository().create(event.player.toTPlayer())
    }

}