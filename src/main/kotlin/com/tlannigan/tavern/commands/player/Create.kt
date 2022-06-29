package com.tlannigan.tavern.commands.player

import com.tlannigan.tavern.models.TCampaign
import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.repositories.PlayerRepository
import com.tlannigan.tavern.utils.buildCharacter
import com.tlannigan.tavern.utils.getTPlayer
import com.tlannigan.tavern.utils.toTLocation
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player

class Create {

    companion object {

        val createCommand: CommandAPICommand =
            CommandAPICommand("create")
                .withArguments(GreedyStringArgument("campaign_name"))
                .withAliases("c")
                .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                    createCampaign(player, args)
                })

        private fun createCampaign(player: Player, args: Array<Any?>) {
            val tPlayer = player.getTPlayer()
            if (tPlayer != null && !tPlayer.hasActiveCampaign()) {
                // Build initial campaign state
                val gameMaster = player.buildCharacter()

                val campaign = TCampaign(
                    name = args[0] as String,
                    gameMaster = gameMaster,
                    spawn = player.location.toTLocation()
                )

                val savedCampaign = CampaignRepository().create(campaign)

                if (savedCampaign.insertedId != null) {
                    // Update TPlayer with new campaign
                    tPlayer.campaigns.add(campaign.id)
                    val updatedPlayer = PlayerRepository().update(tPlayer)

                    if (updatedPlayer.modifiedCount > 0) {
                        player.sendMessage("Campaign ${args[0]} created!")
                    } else {
                        player.sendMessage("There was an issue adding the campaign to your account.")
                    }
                } else {
                    player.sendMessage("There was an issue creating the campaign.")
                }
            } else {
                player.sendMessage("You must leave your current campaign session first.")
            }
        }

    }

}