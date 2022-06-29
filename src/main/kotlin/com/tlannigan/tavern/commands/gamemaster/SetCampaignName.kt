package com.tlannigan.tavern.commands.gamemaster

import com.tlannigan.tavern.utils.Strings
import com.tlannigan.tavern.utils.getTPlayer
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player

object SetCampaignName {

    val setCampaignNameCommand: CommandAPICommand =
        CommandAPICommand("settings")
            .withArguments(LiteralArgument("name"))
            .withArguments(GreedyStringArgument("campaign_name"))
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                setCampaignName(player, args)
            })

    private fun setCampaignName(player: Player, args: Array<Any?>) {
        val gameMaster = player.getTPlayer()
        if (gameMaster != null && gameMaster.hasActiveCampaign()) {
            val campaign = gameMaster.getActiveCampaign()

            if (campaign != null && campaign.gameMaster.uuid == player.uniqueId) {
                campaign.name = args[0] as String
                campaign.update()
            } else {
                player.sendMessage(Strings.GAME_MASTER_REQUIRED)
            }
        } else {
            player.sendMessage(Strings.ACTIVE_CAMPAIGN_REQUIRED)
        }
    }

}
