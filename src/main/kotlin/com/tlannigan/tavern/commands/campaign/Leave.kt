package com.tlannigan.tavern.commands.campaign

import com.tlannigan.tavern.utils.applyState
import com.tlannigan.tavern.utils.getPlayerState
import com.tlannigan.tavern.utils.getTPlayer
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.GameMode
import org.bukkit.entity.Player

object Leave {

    val leaveCommand: CommandAPICommand =
        CommandAPICommand("leave")
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
                        campaign.update()

                        tPlayer.activeCampaign = null
                        tPlayer.update()

                        // Apply saved overworld state
                        player.applyState(tPlayer.state)
                    } else {
                        val campaignCharacter = campaign.characters.find { it.uuid == player.uniqueId }
                        if (campaignCharacter != null) {
                            // Save campaign state
                            campaignCharacter.state = campaignState
                            campaign.update()

                            tPlayer.activeCampaign = null
                            tPlayer.update()

                            // Apply saved overworld state
                            player.gameMode = GameMode.SURVIVAL
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
