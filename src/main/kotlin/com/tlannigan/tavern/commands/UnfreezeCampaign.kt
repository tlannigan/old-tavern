package com.tlannigan.tavern.commands

import com.tlannigan.tavern.utils.Strings
import com.tlannigan.tavern.utils.getTPlayer
import com.tlannigan.tavern.utils.unfreeze
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object UnfreezeCampaign {

    val unfreezeCampaignCommand: CommandAPICommand =
        CommandAPICommand("freeze")
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                unfreezeCampaign(player, args)
            })

    private fun unfreezeCampaign(player: Player, args: Array<Any?>) {
        val tPlayer = player.getTPlayer()
        val campaign = tPlayer?.getActiveCampaign()

        if (campaign != null) {
            if (campaign.gameMaster.uuid == player.uniqueId) {
                val charactersInSession = campaign.characters.filter { it.inSession }

                if (charactersInSession.isNotEmpty()) {
                    for (character in charactersInSession) {
                        val targetPlayer = Bukkit.getPlayer(character.uuid)
                        targetPlayer?.unfreeze()
                    }
                } else {
                    player.sendMessage(Strings.CHARACTER_NOT_FOUND)
                }
            } else {
                player.sendMessage(Strings.GAME_MASTER_REQUIRED)
            }
        } else {
            player.sendMessage(Strings.ACTIVE_CAMPAIGN_REQUIRED)
        }
    }

}
