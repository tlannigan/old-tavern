package com.tlannigan.tavern.commands.gamemaster

import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.utils.getTPlayer
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.SafeSuggestions
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class Start {

    companion object {

        val startCommand: CommandAPICommand =
            CommandAPICommand("start")
                .withArguments(
                    GreedyStringArgument("campaign_name")
                    .replaceSafeSuggestions(SafeSuggestions.suggestAsync { info ->
                        CompletableFuture.supplyAsync {
                            val player = info.sender() as Player
                            val tPlayer = player.getTPlayer()
                            if (tPlayer != null) {
                                val campaignsInSession = CampaignRepository()
                                    .findManyWhereGameMasterAndNotInSession(tPlayer.campaigns, tPlayer.id)

                                campaignsInSession.map { it.name }.toTypedArray()
                            } else {
                                arrayOf()
                            }
                        }
                    })
                )
                .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                    startCampaign(player, args)
                })

        private fun startCampaign(player: Player, args: Array<Any?>) {
            val gameMaster = player.getTPlayer()
            if (gameMaster != null && !gameMaster.hasActiveCampaign()) {
                val campaignName = args[0] as String
                val campaigns = CampaignRepository().findMany(gameMaster.campaigns)
                val campaign = campaigns.find { it.name == campaignName }

                if (campaign != null) {
                    if (campaign.gameMaster.uuid == player.uniqueId) {
                        campaign.inSession = true
                        val updatedCampaign = CampaignRepository().update(campaign)

                        if (updatedCampaign.modifiedCount > 0) {
                            player.sendMessage("Starting campaign...")
                            player.performCommand("campaign enter $campaignName")
                        }
                    } else {
                        player.sendMessage("You are not the Game Master of this campaign.")
                    }
                } else {
                    player.sendMessage("A campaign with this name does not exist.")
                }
            } else {
                player.sendMessage("You must leave your current campaign session first.")
            }
        }

    }

}