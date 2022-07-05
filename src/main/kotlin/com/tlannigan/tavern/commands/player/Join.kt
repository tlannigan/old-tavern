package com.tlannigan.tavern.commands.player

import com.tlannigan.tavern.models.TCampaign
import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.utils.buildCharacter
import com.tlannigan.tavern.utils.getTPlayer
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player
import org.litote.kmongo.toId

object Join {

    val joinCommand: CommandAPICommand =
        CommandAPICommand("join")
            .withArguments(StringArgument("campaign"))
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                joinCampaign(player, args)
            })

    private fun joinCampaign(player: Player, args: Array<Any?>) {
        val tPlayer = player.getTPlayer()
        if (tPlayer != null) {
            val stringId = args[0] as String
            val campaignId = stringId.toId<TCampaign>()
            var campaign = CampaignRepository().find(campaignId)

            if (campaign != null) {
                val character = player.buildCharacter(campaign.spawn)
                campaign.characters.add(character)
                campaign.update()

                tPlayer.campaigns.add(campaign.id)
                tPlayer.update()
            }
        }
    }

}
