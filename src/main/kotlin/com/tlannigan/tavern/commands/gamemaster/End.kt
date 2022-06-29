package com.tlannigan.tavern.commands.gamemaster

import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.utils.getTPlayer
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class End {

    companion object {

        val endCommand: CommandAPICommand =
            CommandAPICommand("end")
                .executesPlayer(PlayerCommandExecutor { player: Player, _ ->
                    endCampaign(player)
                })

        private fun endCampaign(player: Player) {
            val gameMaster = player.getTPlayer()
            if (gameMaster != null) {
                val campaign = gameMaster.getActiveCampaign()
                if (campaign != null) {
                    campaign.inSession = false
                    val updatedCampaign = CampaignRepository().update(campaign)

                    if (updatedCampaign.modifiedCount > 0) {
                        player.sendMessage("Ending campaign session...")
                        player.performCommand("leave")

                        val playersInSession = campaign.getPlayersInSession()
                        playersInSession.forEach {
                            val bukkitPlayer = it.getBukkitPlayer()
                            if (bukkitPlayer != null) {
                                bukkitPlayer.performCommand("leave")
                            } else {
                                Bukkit.getConsoleSender()
                                    .sendMessage("There was an error allowing ${it.id} to leave campaign session")
                            }
                        }
                    }
                } else {
                    player.sendMessage("You are not in a campaign session.")
                }
            }
        }

    }

}