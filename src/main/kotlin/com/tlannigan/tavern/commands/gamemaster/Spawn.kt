package com.tlannigan.tavern.commands.gamemaster

import com.tlannigan.tavern.utils.Strings
import com.tlannigan.tavern.utils.getTPlayer
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player

object Spawn {

    val spawnCommand: CommandAPICommand =
        CommandAPICommand("spawn")
            .executesPlayer(PlayerCommandExecutor { player: Player, _ ->
                spawn(player)
            })

    private fun spawn(player: Player) {
        val gameMaster = player.getTPlayer()
        if (gameMaster != null && gameMaster.hasActiveCampaign()) {
            val campaign = gameMaster.getActiveCampaign()
            if (campaign != null) {
                val spawn = campaign.spawn.toLocation()
                player.sendMessage(Strings.TELEPORT_SPAWN)
                player.teleport(spawn)
            } else {
                player.sendMessage(Strings.INTERNAL_ERROR)
            }
        } else {
            player.sendMessage(Strings.ACTIVE_CAMPAIGN_REQUIRED)
        }
    }

}
