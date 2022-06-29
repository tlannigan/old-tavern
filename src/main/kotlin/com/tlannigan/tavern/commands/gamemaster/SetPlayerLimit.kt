package com.tlannigan.tavern.commands.gamemaster

import com.tlannigan.tavern.utils.Strings
import com.tlannigan.tavern.utils.getTPlayer
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player

object SetPlayerLimit {

    val setPlayerLimitCommand: CommandAPICommand =
        CommandAPICommand("settings")
            .withArguments(LiteralArgument("player_limit"))
            .withArguments(IntegerArgument("amount"))
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                setPlayerLimit(player, args)
            })

    private fun setPlayerLimit(player: Player, args: Array<Any?>) {
        val gameMaster = player.getTPlayer()
        if (gameMaster != null && gameMaster.hasActiveCampaign()) {
            val campaign = gameMaster.getActiveCampaign()

            if (campaign != null && campaign.gameMaster.uuid == player.uniqueId) {
                campaign.playerLimit = args[0] as Int
                campaign.update()
            } else {
                player.sendMessage(Strings.GAME_MASTER_REQUIRED)
            }
        } else {
            player.sendMessage(Strings.ACTIVE_CAMPAIGN_REQUIRED)
        }
    }

}
