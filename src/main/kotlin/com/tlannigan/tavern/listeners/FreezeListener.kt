package com.tlannigan.tavern.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class FreezeListener : Listener {

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (event.player.scoreboardTags.contains("frozen")) {
            if (
                event.from.x != event.to.x ||
                event.from.y != event.to.y ||
                event.from.z != event.to.z
            ) {
                event.isCancelled = true
            }
        }
    }

}
