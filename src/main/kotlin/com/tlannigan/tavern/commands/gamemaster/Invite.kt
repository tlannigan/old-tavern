package com.tlannigan.tavern.commands.gamemaster

import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.utils.ChatComponents
import com.tlannigan.tavern.utils.getTPlayer
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

object Invite {

    val inviteCommand: CommandAPICommand =
        CommandAPICommand("invite")
            .withArguments(PlayerArgument("player"))
            .withArguments(
                GreedyStringArgument("campaign_name")
                    .replaceSuggestions(ArgumentSuggestions.stringsAsync { info ->
                        CompletableFuture.supplyAsync {
                            val player = info.sender() as Player
                            val tPlayer = player.getTPlayer()
                            if (tPlayer != null) {
                                val campaignsInSession = CampaignRepository()
                                    .findManyWhereGameMaster(tPlayer.campaigns, player.uniqueId)
                                campaignsInSession.map { it.name }.toTypedArray()
                            } else {
                                arrayOf()
                            }
                        }
                    })
            )
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                inviteCampaign(player, args)
            })

    private fun inviteCampaign(player: Player, args: Array<Any?>) {
        val gameMaster = player.getTPlayer()
        if (gameMaster != null) {
            val invitee = args[0] as Player
            if (invitee.uniqueId != player.uniqueId) {
                val campaignName = args[1] as String
                val campaigns = CampaignRepository()
                    .findManyWhereGameMaster(gameMaster.campaigns, player.uniqueId)
                val campaign = campaigns.find { it.name == campaignName }

                if (campaign != null) {
                    if (campaign.gameMaster.uuid == player.uniqueId) {
                        invitee.sendMessage(ChatComponents.inviteCampaign(campaign))
                    } else {
                        player.sendMessage("You must be the game master to invite players.")
                    }
                } else {
                    player.sendMessage("Could not find a campaign with that name")
                }
            } else {
                player.sendMessage("Silly Billy, you can't invite yourself!")
            }
        }
    }

}
