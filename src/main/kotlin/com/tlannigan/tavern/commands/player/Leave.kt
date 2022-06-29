package com.tlannigan.tavern.commands.player

import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.repositories.PlayerRepository
import com.tlannigan.tavern.utils.applyState
import com.tlannigan.tavern.utils.getPlayerState
import com.tlannigan.tavern.utils.getTPlayer
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player

class Leave {

    companion object {

        val leaveCommand: CommandAPICommand =
            CommandAPICommand("leave")
                .withAliases("l")
                .executesPlayer(PlayerCommandExecutor { player: Player, _ ->
                    leaveCampaign(player)
                })

        private fun leaveCampaign(player: Player) {
            val tPlayer = player.getTPlayer()
            if (tPlayer != null && tPlayer.hasActiveCampaign()) {
                val campaign = tPlayer.getActiveCampaign()

                if (campaign != null) {
                    if (campaign.gameMaster.uuid == player.uniqueId && campaign.inSession) {
                        player.performCommand("gm end")
                    } else {
                        val campaignState = player.getPlayerState()

                        // Check if player is Game Master or player
                        // and save campaign state appropriately
                        if (campaign.gameMaster.uuid == player.uniqueId) {
                            campaign.gameMaster.state = campaignState
                            CampaignRepository().update(campaign)

                            tPlayer.activeCampaign = null
                            PlayerRepository().update(tPlayer)

                            // Apply saved overworld state
                            player.applyState(tPlayer.state)
                        } else {
                            val campaignCharacter = campaign.characters.find { it.uuid == player.uniqueId }
                            if (campaignCharacter != null) {
                                // Save campaign state
                                campaignCharacter.state = campaignState
                                CampaignRepository().update(campaign)

                                tPlayer.activeCampaign = null
                                PlayerRepository().update(tPlayer)

                                // Apply saved overworld state
                                player.applyState(tPlayer.state)
                            } else {
                                player.sendMessage("There was an issue finding your character in this campaign.")
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