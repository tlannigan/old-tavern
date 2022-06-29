package com.tlannigan.tavern.commands.gamemaster

import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.utils.Strings
import com.tlannigan.tavern.utils.getTPlayer
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.BooleanArgument
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player

object SetPrivacy {

    val setPrivacyCommand: CommandAPICommand =
        CommandAPICommand("settings")
            .withArguments(LiteralArgument("public"))
            .withArguments(BooleanArgument("boolean"))
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                setPrivacy(player, args)
            })

    private fun setPrivacy(player: Player, args: Array<Any?>) {
        val gameMaster = player.getTPlayer()
        if (gameMaster != null && gameMaster.hasActiveCampaign()) {
            val campaign = gameMaster.getActiveCampaign()

            if (campaign != null && campaign.gameMaster.uuid == player.uniqueId) {
                campaign.isPublic = args[0] as Boolean
                CampaignRepository().update(campaign)
            } else {
                player.sendMessage(Strings.GAME_MASTER_REQUIRED)
            }
        } else {
            player.sendMessage(Strings.ACTIVE_CAMPAIGN_REQUIRED)
        }
    }

}
