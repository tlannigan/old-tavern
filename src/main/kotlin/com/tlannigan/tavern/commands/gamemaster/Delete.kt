package com.tlannigan.tavern.commands.gamemaster

import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.utils.Strings
import com.tlannigan.tavern.utils.getTPlayer
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

object Delete {

    val deleteCommand: CommandAPICommand =
        CommandAPICommand("delete")
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
                deleteCampaign(player, args)
            })

    private fun deleteCampaign(player: Player, args: Array<Any?>) {
        val gameMaster = player.getTPlayer()
        if (gameMaster != null) {
            val campaignName = args[0] as String
            val campaigns = CampaignRepository()
                .findManyWhereGameMaster(gameMaster.campaigns, player.uniqueId)
            val campaign = campaigns.find { it.name == campaignName }

            if (campaign != null) {
                if (campaign.gameMaster.uuid == player.uniqueId) {
                    if (campaign.inSession) {
                        player.performCommand("gm end")
                    }

                    campaign.characters.forEach {
                        val campaignPlayer = Bukkit.getPlayer(it.uuid)
                        player.performCommand("gm kick ${campaignPlayer?.name}")
                    }

                    gameMaster.campaigns.remove(campaign.id)
                    gameMaster.update()

                    val deleted = campaign.delete()

                    if (deleted.deletedCount == 1L) {
                        player.sendMessage(Strings.CAMPAIGN_DELETED)
                    } else {
                        player.sendMessage(Strings.INTERNAL_ERROR)
                    }
                } else {
                    player.sendMessage(Strings.GAME_MASTER_REQUIRED)
                }
            } else {
                player.sendMessage(Strings.INTERNAL_ERROR)
            }
        } else {
            player.sendMessage(Strings.INTERNAL_ERROR)
        }
    }

}
