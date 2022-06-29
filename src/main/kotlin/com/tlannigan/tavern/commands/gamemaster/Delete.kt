package com.tlannigan.tavern.commands.gamemaster

import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.repositories.PlayerRepository
import com.tlannigan.tavern.utils.getTPlayer
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class Delete {

    companion object {

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
                .withAliases("d")
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
                        player.performCommand("end $campaignName")

                        campaign.characters.forEach {
                            val campaignPlayer = Bukkit.getPlayer(it.uuid)
                            player.performCommand("kick ${campaignPlayer?.name}")
                        }

                        gameMaster.campaigns.remove(campaign.id)
                        PlayerRepository().update(gameMaster)

                        val deleted = CampaignRepository().delete(campaign)
                        if (deleted.deletedCount == 1L) {
                            player.sendMessage("Campaign $campaignName deleted.")
                        } else {
                            player.sendMessage("An error occurred while deleting this campaign.")
                        }
                    } else {
                        player.sendMessage("You must be the Game Master to delete this campaign.")
                    }
                } else {
                    player.sendMessage("This campaign has already been deleted.")
                }
            } else {
                player.sendMessage("You must enter a campaign before using /gm delete")
            }
        }

    }

}