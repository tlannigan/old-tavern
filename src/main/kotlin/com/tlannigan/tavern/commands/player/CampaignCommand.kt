package com.tlannigan.tavern.commands.player

import com.tlannigan.tavern.models.PlayerState
import com.tlannigan.tavern.models.TCampaign
import com.tlannigan.tavern.models.TPlayer
import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.repositories.PlayerRepository
import com.tlannigan.tavern.utils.*
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class CampaignCommand : TabExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender is Player) {
            if (args.isNotEmpty()) {
                val tPlayer = sender.getTPlayer()
                if (tPlayer != null) {
                    when (args[0].lowercase()) {
                        "create" -> createCampaign(sender, tPlayer, args)
                        "enter" -> enterCampaign(sender, tPlayer, args)
                        "leave" -> leaveCampaign(sender, tPlayer)
                    }

                    return true
                } else {
                    Bukkit
                        .getConsoleSender()
                        .sendMessage(
                            "An unexpected error occurred finding this player: ${sender.name} ${sender.uniqueId}"
                        )
                }
            }
        }

        return false
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String>? {
        if (sender is Player) {
            if (args != null) {
                if (args.size == 1) {
                    return mutableListOf("create", "enter", "leave")
                } else if (args.size > 1) {
                    if (args[1].lowercase() == "enter") {
                        val tPlayer = sender.getTPlayer()
                        if (tPlayer != null && args[1].isEmpty()) {
                            val campaigns = CampaignRepository().findManyInSession(tPlayer.campaigns)
                            return campaigns.map { it.name }.toMutableList()
                        }
                    }
                }
            }
        }

        return null
    }

    fun createCampaign(player: Player, tPlayer: TPlayer, args: Array<out String>) {
        if (!tPlayer.hasActiveCampaign()) {
            // Build initial campaign state
            val gameMaster = player.buildCharacter()

            val campaign = TCampaign(
                name = args[1],
                gameMaster = gameMaster,
                spawn = player.location.toTLocation()
            )

            val savedCampaign = CampaignRepository().create(campaign)

            if (savedCampaign.insertedId != null) {
                // Update TPlayer with new campaign
                tPlayer.campaigns.add(campaign.id)
                val updatedPlayer = PlayerRepository().update(tPlayer)

                if (updatedPlayer.modifiedCount > 0) {
                    player.sendMessage("Campaign ${args[1]} created!")
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

    fun enterCampaign(player: Player, tPlayer: TPlayer, args: Array<out String>) {
        if (!tPlayer.hasActiveCampaign()) {
            val campaignName = args[1]
            val campaigns = CampaignRepository().findMany(tPlayer.campaigns)
            val campaign = campaigns.find { it.name == campaignName }

            if (campaign != null) {
                if (campaign.gameMaster.uuid == player.uniqueId && !campaign.inSession) {
                    player.performCommand("gm start $campaignName")
                } else if (campaign.inSession) {
                    // Store player's overworld state
                    tPlayer.state = player.getPlayerState()
                    tPlayer.activeCampaign = campaign.id
                    PlayerRepository().update(tPlayer)

                    // Set player character inSession to true
                    val character = campaign.characters.find { it.uuid == player.uniqueId }
                    if (character != null) {
                        character.inSession = true
                        CampaignRepository().update(campaign)
                    }

                    val campaignState: PlayerState?

                    // Check if player is Game Master or player
                    // and apply relevant state
                    if (campaign.gameMaster.uuid == player.uniqueId) {
                        campaignState = campaign.gameMaster.state
                        player.applyState(campaignState)
                    } else {
                        val campaignCharacter = campaign.characters.find { it.uuid == player.uniqueId }
                        if (campaignCharacter != null) {
                            // Fetch player's state in campaign
                            campaignState = campaignCharacter.state
                            player.applyState(campaignState)
                        } else {
                            player.sendMessage("Could not find your character in this campaign.")
                        }
                    }
                }
            } else {
                player.sendMessage("A campaign with this name does not exist.")
            }
        } else {
            player.sendMessage("You must leave your current campaign session first.")
        }
    }

    fun leaveCampaign(player: Player, tPlayer: TPlayer) {
        val campaign = tPlayer.getActiveCampaign()

        if (campaign != null) {
            if (campaign.gameMaster.uuid == player.uniqueId && campaign.inSession) {
                player.performCommand("gm end")
            } else {
                val campaignState = player.getPlayerState()

                // Check if player is Game Master or player
                // and save campaign state appropriately
                if (campaign.gameMaster.uuid == player.uniqueId) {
                    campaign.gameMaster.state = campaignState
                    CampaignRepository().update(campaign)

                    tPlayer.activeCampaign = null
                    PlayerRepository().update(tPlayer)

                    // Apply saved overworld state
                    player.applyState(tPlayer.state)
                } else {
                    val campaignCharacter = campaign.characters.find { it.uuid == player.uniqueId }
                    if (campaignCharacter != null) {
                        // Save campaign state
                        campaignCharacter.state = campaignState
                        CampaignRepository().update(campaign)

                        tPlayer.activeCampaign = null
                        PlayerRepository().update(tPlayer)

                        // Apply saved overworld state
                        player.applyState(tPlayer.state)
                    } else {
                        player.sendMessage("There was an issue finding your character in this campaign.")
                    }
                }
            }
        } else {
            player.sendMessage("You are not in a campaign session.")
        }
    }

}
