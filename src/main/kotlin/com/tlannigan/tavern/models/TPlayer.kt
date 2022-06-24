package com.tlannigan.tavern.models

import com.tlannigan.tavern.repositories.CampaignRepository
import com.tlannigan.tavern.repositories.PlayerRepository
import com.tlannigan.tavern.utils.ChatComponents.Companion.inviteCampaign
import com.tlannigan.tavern.utils.applyState
import com.tlannigan.tavern.utils.getPlayerState
import com.tlannigan.tavern.utils.toTLocation
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getConsoleSender
import org.bukkit.Bukkit.getWorld
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.litote.kmongo.Id
import java.util.*

@Serializable
data class TPlayer(

    @Contextual
    @SerialName("_id")
    val id: UUID,

    var state: PlayerState,

    var activeCampaign: @Contextual Id<TCampaign>? = null,

    val campaigns: MutableList<@Contextual Id<TCampaign>> = mutableListOf(),

    val characters: MutableList<@Contextual Id<TCharacter>> = mutableListOf()

) {

    fun createCampaign(args: Array<out String>, player: Player? = getBukkitPlayer()) {
        if (player != null) {
            val gameMaster = buildCharacter(player)

            val campaign = TCampaign(
                name = args[1],
                gameMaster = gameMaster,
                spawn = player.location.toTLocation()
            )

            val savedCampaign = CampaignRepository().create(campaign)

            if (savedCampaign.insertedId != null) {
                this.campaigns.add(campaign.id)
                val updatedPlayer = PlayerRepository().update(this)

                if (updatedPlayer.modifiedCount > 0) {
                    player.sendMessage("Campaign ${args[1]} created!")
                } else {
                    player.sendMessage("There was an issue adding the campaign to your account.")
                }
            } else {
                player.sendMessage("There was an issue creating the campaign.")
            }
        } else {
            getConsoleSender().sendMessage("Player $id doesn't exist")
        }
    }

    fun deleteCampaign(args: Array<out String>, player: Player? = getBukkitPlayer()) {
        if (player != null) {
            if (activeCampaign != null) {
                if (args.size > 1 && args[1].lowercase() == "true") {
                    val campaign = CampaignRepository().find(activeCampaign!!)

                    if (campaign != null) {
                        if (campaign.gameMaster.uuid == this.id) {
                            this.endCampaign()

                            campaign.characters.forEach {
                                campaign.kickPlayer(it.name)
                            }

                            campaigns.remove(campaign.id)
                            PlayerRepository().update(this)

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
        } else {
            getConsoleSender().sendMessage("Player $id doesn't exist.")
        }
    }

    fun inviteCampaign(args: Array<out String>, player: Player? = getBukkitPlayer()) {
        if (player != null) {
            if (activeCampaign != null) {
                val playerName = args[1]
                val invitee = Bukkit.getPlayerExact(playerName)
                if (invitee != null) {
                    if (invitee.uniqueId != player.uniqueId) {
                        val campaign = CampaignRepository().find(activeCampaign!!)
                        if (campaign != null) {
                            invitee.sendMessage(inviteCampaign(campaign))
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
        } else {
            getConsoleSender().sendMessage("Player $id doesn't exist.")
        }
    }

    fun startCampaign(args: Array<out String>, player: Player? = getBukkitPlayer()) {
        if (player != null) {
            if (activeCampaign == null) {
                val campaignName = args[1]
                val campaigns = CampaignRepository().findMany(this.campaigns)
                val campaign = campaigns.find { it.name == campaignName }

                if (campaign != null) {
                    if (campaign.gameMaster.uuid == this.id) {
                        campaign.inSession = true
                        val updatedCampaign = CampaignRepository().update(campaign)

                        if (updatedCampaign.modifiedCount > 0) {
                            player.sendMessage("Starting campaign...")
                            this.enterCampaign(args, player)
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
        } else {
            getConsoleSender().sendMessage("Player $id doesn't exist.")
        }
    }

    fun endCampaign(player: Player? = getBukkitPlayer()) {
        if (player != null) {
            if (activeCampaign != null) {
                val campaign = CampaignRepository().find(activeCampaign!!)
                if (campaign != null) {
                    if (campaign.gameMaster.uuid == this.id) {
                        campaign.inSession = false
                        val updatedCampaign = CampaignRepository().update(campaign)

                        if (updatedCampaign.modifiedCount > 0) {
                            player.sendMessage("Ending campaign...")
                            this.leaveCampaign()
                        }
                    } else {
                        player.sendMessage("You are not the Game Master of this campaign.")
                    }
                } else {
                    player.sendMessage("This campaign has been deleted.")
                }
            } else {
                player.sendMessage("You are not in a campaign session.")
            }
        } else {
            getConsoleSender().sendMessage("Player $id doesn't exist.")
        }
    }

    fun enterCampaign(args: Array<out String>, player: Player? = getBukkitPlayer()) {
        if (player != null) {
            if (activeCampaign == null) {
                val campaignName = args[1]
                val campaigns = CampaignRepository().findMany(this.campaigns)
                val campaign = campaigns.find { it.name == campaignName }

                if (campaign != null) {
                    if (campaign.gameMaster.uuid == this.id && !campaign.inSession) {
                        this.startCampaign(args)
                    } else {
                        // Store player's overworld state
                        this.state = player.getPlayerState()
                        this.activeCampaign = campaign.id
                        PlayerRepository().update(this)

                        val campaignState: PlayerState?

                        // Check if player is Game Master or player
                        // and apply relevant state
                        if (campaign.gameMaster.uuid == this.id) {
                            campaignState = campaign.gameMaster.state
                            player.applyState(campaignState)
                        } else {
                            val campaignCharacter = campaign.characters.find { it.uuid == this.id }
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
        } else {
            getConsoleSender().sendMessage("Player $id doesn't exist.")
        }
    }

    fun leaveCampaign(player: Player? = getBukkitPlayer()) {
        if (player != null) {
            if (activeCampaign != null) {
                val campaign = CampaignRepository().find(activeCampaign!!)

                if (campaign != null) {
                    if (campaign.gameMaster.uuid == this.id && campaign.inSession) {
                        this.endCampaign()
                    } else {
                        val campaignState = player.getPlayerState()

                        // Check if player is Game Master or player
                        // and save campaign state appropriately
                        if (campaign.gameMaster.uuid == this.id) {
                            campaign.gameMaster.state = campaignState
                            CampaignRepository().update(campaign)

                            this.activeCampaign = null
                            PlayerRepository().update(this)

                            // Apply saved overworld state
                            player.applyState(this.state)
                        } else {
                            val campaignCharacter = campaign.characters.find { it.uuid == this.id }
                            if (campaignCharacter != null) {
                                // Save campaign state
                                campaignCharacter.state = campaignState
                                CampaignRepository().update(campaign)

                                this.activeCampaign = null
                                PlayerRepository().update(this)

                                // Apply saved overworld state
                                player.applyState(this.state)
                            } else {
                                player.sendMessage("There was an issue finding your character in this campaign.")
                            }
                        }
                    }
                } else {
                    player.sendMessage("This campaign has been deleted.")
                }
            } else {
                player.sendMessage("You are not in a campaign session.")
            }
        } else {
            getConsoleSender().sendMessage("Player $id doesn't exist.")
        }
    }

    fun buildCharacter(player: Player): TCharacter {
        return TCharacter(
            uuid = player.uniqueId,
            name = player.name,
            state = player.getPlayerState()
        )
    }

    fun getBukkitPlayer(): Player? {
        return Bukkit.getPlayer(this.id)
    }

}

@Serializable
data class PlayerState(

    val health: Double,

    val mana: Int,

    val location: TLocation,

    val inventory: Inventory? = null

)

@Serializable
data class TLocation(

    val world: String,

    val x: Double,

    val y: Double,

    val z: Double,

    val yaw: Float,

    val pitch: Float

) {

    /**
     * Converts serialized location into Bukkit Location
     */
    fun toLocation(): Location {
        return Location(
            getWorld(world),
            this.x,
            this.y,
            this.z,
            this.yaw,
            this.pitch
        )
    }
}
