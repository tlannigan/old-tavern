package com.tlannigan.tavern.commands.gamemaster

import com.tlannigan.tavern.models.TPlayer
import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.repositories.PlayerRepository
import com.tlannigan.tavern.utils.ChatComponents
import com.tlannigan.tavern.utils.getTPlayer
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getConsoleSender
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class GameMasterCommand : TabExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender is Player) {
            if (args.isNotEmpty()) {
                val tPlayer = sender.getTPlayer()
                val ownedCampaigns = tPlayer?.getCampaignsWhereGameMaster()
                if (!ownedCampaigns.isNullOrEmpty()) {
                    when (args[0].lowercase()) {
                        "start" -> startCampaign(sender, tPlayer, args)
                        "end" -> endCampaign(sender, tPlayer)
                        "invite" -> inviteCampaign(sender, tPlayer, args)
                        "delete" -> deleteCampaign(sender, tPlayer, args)
                    }

                    return true
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
                    return mutableListOf("start", "end", "invite", "delete")
                } else if (args.size > 1) {
                    if (args[0] == "start") {
                        val tPlayer = sender.getTPlayer()
                        if (tPlayer != null && args[1].isEmpty()) {
                            val campaigns = CampaignRepository().findMany(tPlayer.campaigns)
                            return campaigns.map { it.name }.toMutableList()
                        }
                    }
                }
            }
        }

        return null
    }

    fun startCampaign(player: Player, gameMaster: TPlayer, args: Array<out String>) {
        if (!gameMaster.hasActiveCampaign()) {
            val campaignName = args[1]
            val campaigns = CampaignRepository().findMany(gameMaster.campaigns)
            val campaign = campaigns.find { it.name == campaignName }

            if (campaign != null) {
                if (campaign.gameMaster.uuid == player.uniqueId) {
                    campaign.inSession = true
                    val updatedCampaign = CampaignRepository().update(campaign)

                    if (updatedCampaign.modifiedCount > 0) {
                        player.sendMessage("Starting campaign...")
                        player.performCommand("campaign enter $campaignName")
                    }
                } else {
                    player.sendMessage("You are not the Game Master of this campaign.")
                }
            } else {
                player.sendMessage("A campaign with this name does not exist.")
            }
        } else {
            player.sendMessage("You must leave your current campaign session first.")
        }
    }

    fun endCampaign(player: Player, gameMaster: TPlayer) {
        val campaign = gameMaster.getActiveCampaign()
        if (campaign != null) {
            campaign.inSession = false
            val updatedCampaign = CampaignRepository().update(campaign)

            if (updatedCampaign.modifiedCount > 0) {
                player.sendMessage("Ending campaign...")
                player.performCommand("campaign leave")

                val playersInSession = campaign.getPlayersInSession()
                playersInSession.forEach {
                    val bukkitPlayer = it.getBukkitPlayer()
                    if (bukkitPlayer != null) {
                        bukkitPlayer.performCommand("campaign leave")
                    } else {
                        getConsoleSender()
                            .sendMessage("There was an error allowing ${it.id} to leave campaign session")
                    }
                }
            }
        } else {
            player.sendMessage("You are not in a campaign session.")
        }
    }

    fun inviteCampaign(player: Player, gameMaster: TPlayer, args: Array<out String>) {
        if (gameMaster.hasActiveCampaign()) {
            val playerName = args[1]
            val invitee = Bukkit.getPlayerExact(playerName)
            if (invitee != null) {
                if (invitee.uniqueId != player.uniqueId) {
                    val campaign = CampaignRepository().find(gameMaster.activeCampaign!!)
                    if (campaign != null) {
                        invitee.sendMessage(ChatComponents.inviteCampaign(campaign))
                    }
                } else {
                    player.sendMessage("Silly Billy, you can't invite yourself!")
                }
            } else {
                player.sendMessage("Could not find that player.")
            }
        } else {
            player.sendMessage("Enter a campaign to invite others.")
        }
    }

    fun kickPlayer(player: Player, gameMaster: TPlayer, args: Array<out String>): Boolean {
        if (gameMaster.hasActiveCampaign()) {
            val campaign = gameMaster.getActiveCampaign()
            if (campaign != null) {
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

                        return true
                    }
                }
            }
        }

        return false
    }

    fun deleteCampaign(player: Player, gameMaster: TPlayer, args: Array<out String>) {
        if (gameMaster.hasActiveCampaign()) {
            if (args.size > 1 && args[1].lowercase() == "true") {
                val campaign = gameMaster.getActiveCampaign()

                if (campaign != null) {
                    if (campaign.gameMaster.uuid == player.uniqueId) {
                        endCampaign(player, gameMaster)

                        campaign.characters.forEach {
                            val campaignPlayer = Bukkit.getPlayer(it.uuid)
                            player.performCommand("gm kick ${campaignPlayer?.name}")
                        }

                        gameMaster.campaigns.remove(campaign.id)
                        PlayerRepository().update(gameMaster)

                        player.sendMessage("Deleting campaign...")
                        CampaignRepository().delete(campaign)
                    }
                } else {
                    player.sendMessage("This campaign has already been deleted.")
                }
            } else {
                player.sendMessage("Are you sure you want to delete this campaign? Use /gm delete true")
            }
        } else {
            player.sendMessage("You must enter a campaign before using /gm delete")
        }
    }

}
