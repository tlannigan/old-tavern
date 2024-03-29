package com.tlannigan.tavern.commands.gamemaster

import com.tlannigan.tavern.utils.Strings
import com.tlannigan.tavern.utils.getTPlayer
import com.tlannigan.tavern.utils.toTLocation
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player

object SetSpawn {

    val setSpawnCommand: CommandAPICommand =
        CommandAPICommand("setspawn")
            .executesPlayer(PlayerCommandExecutor { player: Player, _ ->
                setSpawn(player)
            })

    private fun setSpawn(player: Player) {
        val gameMaster = player.getTPlayer()
        if (gameMaster != null && gameMaster.hasActiveCampaign()) {
            val campaign = gameMaster.getActiveCampaign()

            if (campaign != null) {
                if (campaign.gameMaster.uuid == player.uniqueId) {
                    val newSpawn = player.location.toTLocation()
                    campaign.spawn = newSpawn
                    val updated = campaign.update()

                    if (updated.modifiedCount == 1L) {
                        player.sendMessage(Strings.SPAWN_UPDATED)
                    }
                } else {
                    player.sendMessage(Strings.GAME_MASTER_REQUIRED)
                }
            }
        } else {
            player.sendMessage(Strings.ACTIVE_CAMPAIGN_REQUIRED)
        }
    }

}
