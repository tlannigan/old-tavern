package com.tlannigan.tavern.commands.player

import com.tlannigan.tavern.models.PlayerState
import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.repositories.PlayerRepository
import com.tlannigan.tavern.utils.applyState
import com.tlannigan.tavern.utils.getPlayerState
import com.tlannigan.tavern.utils.getTPlayer
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

object Enter {

    val enterCommand: CommandAPICommand =
        CommandAPICommand("enter")
            .withArguments(GreedyStringArgument("campaign_name")
                .replaceSuggestions(ArgumentSuggestions.stringsAsync { info ->
                    CompletableFuture.supplyAsync {
                        val player = info.sender() as Player
                        val tPlayer = player.getTPlayer()
                        if (tPlayer != null) {
                            val campaignsInSession = CampaignRepository().findManyInSession(tPlayer.campaigns)
                            campaignsInSession.map { it.name }.toTypedArray()
                        } else {
                            arrayOf()
                        }
                    }
                })
            )
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                enterCampaign(player, args)
            })

    private fun enterCampaign(player: Player, args: Array<Any?>) {
        val tPlayer = player.getTPlayer()
        if (tPlayer != null && !tPlayer.hasActiveCampaign()) {
            val campaignName = args[0] as String
            val campaigns = tPlayer.getAllCampaigns()
            val campaign = campaigns.find { it.name == campaignName }

            if (campaign != null) {
                if (campaign.gameMaster.uuid == player.uniqueId && !campaign.inSession) {
                    player.performCommand("gm start $campaignName")
                } else if (campaign.inSession) {
                    // Store player's overworld state
                    tPlayer.state = player.getPlayerState()
                    tPlayer.activeCampaign = campaign.id
                    PlayerRepository().update(tPlayer)

                    // Set player character inSession to true
                    val character = campaign.characters.find { it.uuid == player.uniqueId }
                    if (character != null) {
                        character.inSession = true
                        campaign.update()
                    }

                    player.sendMessage("Entering campaign session...")

                    val campaignState: PlayerState?

                    // Check if player is Game Master or player
                    // and apply relevant state
                    if (campaign.gameMaster.uuid == player.uniqueId) {
                        campaignState = campaign.gameMaster.state
                        player.applyState(campaignState)
                    } else {
                        val campaignCharacter = campaign.characters.find { it.uuid == player.uniqueId }
                        if (campaignCharacter != null) {
                            // Fetch player's state in campaign
                            campaignState = campaignCharacter.state
                            player.applyState(campaignState)
                        } else {
                            player.sendMessage("Could not find your character in this campaign.")
                        }
                    }
                } else {
                    player.sendMessage("This campaign is not in session.")
                }
            } else {
                player.sendMessage("A campaign with this name does not exist.")
            }
        } else {
            player.sendMessage("You must leave your current campaign session first.")
        }
    }

}
