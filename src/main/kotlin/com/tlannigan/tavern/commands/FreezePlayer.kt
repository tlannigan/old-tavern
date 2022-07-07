package com.tlannigan.tavern.commands

import com.tlannigan.tavern.utils.Strings
import com.tlannigan.tavern.utils.freeze
import com.tlannigan.tavern.utils.getTPlayer
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

object FreezePlayer {

    val freezePlayerCommand: CommandAPICommand =
        CommandAPICommand("freeze")
            .withArguments(StringArgument("character_name")
                .replaceSuggestions(ArgumentSuggestions.stringsAsync { info ->
                    CompletableFuture.supplyAsync {
                        val player = info.sender() as Player
                        val tPlayer = player.getTPlayer()
                        if (tPlayer != null) {
                            val campaign = tPlayer.getActiveCampaign()
                            campaign?.characters?.map { it.name }?.toTypedArray() ?: arrayOf()
                        } else {
                            arrayOf()
                        }
                    }
                })
            )
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?> ->
                freezePlayer(player, args)
            })

    private fun freezePlayer(player: Player, args: Array<Any?>) {
        val tPlayer = player.getTPlayer()
        val campaign = tPlayer?.getActiveCampaign()

        if (campaign != null) {
            if (campaign.gameMaster.uuid == player.uniqueId) {
                val targetCharacterName = args[0] as String
                val targetCharacter = campaign.characters.find { it.name == targetCharacterName }

                if (targetCharacter != null && targetCharacter.inSession) {
                    player.sendMessage(String.format(Strings.CHARACTER_FROZEN, targetCharacter.name))
                    val targetPlayer = Bukkit.getPlayer(targetCharacter.uuid)
                    targetPlayer?.freeze()
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
