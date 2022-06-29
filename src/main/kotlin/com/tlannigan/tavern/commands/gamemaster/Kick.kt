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

object Kick {

    val kickCommand: CommandAPICommand =
        CommandAPICommand("kick")
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
                kickPlayer(player, args)
            })

    private fun kickPlayer(player: Player, args: Array<Any?>) {
        val gameMaster = player.getTPlayer()
        if (gameMaster != null && gameMaster.hasActiveCampaign()) {
            val campaign = gameMaster.getActiveCampaign()
            if (campaign != null && campaign.gameMaster.uuid == player.uniqueId) {
                val characterName = args[1]
                val matchedCharacters = campaign.characters.count { it.name == characterName }

                if (matchedCharacters == 1) {
                    val matchedCharacter = campaign.characters.find { it.name == characterName }

                    val matchedPlayer = Bukkit.getPlayer(matchedCharacter!!.uuid)
                    val tPlayer = player.getTPlayer()

                    if (matchedPlayer != null && tPlayer != null) {
                        val playerActiveCampaign = tPlayer.getActiveCampaign()
                        if (playerActiveCampaign?.id == campaign.id) {
                            player.performCommand("campaign leave")
                        }

                        tPlayer.campaigns.removeAll { it == campaign.id }
                        PlayerRepository().update(tPlayer)

                        campaign.characters.removeAll { it.name == characterName }
                        CampaignRepository().update(campaign)
                    }
                } else {
                    player.sendMessage("Multiple character matched that name, did not delete.")
                }
            } else {
                player.sendMessage("You must be the Game Master to delete this campaign")
            }
        }
    }

}
