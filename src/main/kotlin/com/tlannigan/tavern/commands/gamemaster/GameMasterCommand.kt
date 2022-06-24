package com.tlannigan.tavern.commands.gamemaster

import com.tlannigan.tavern.repositories.CampaignRepository
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
                if (tPlayer != null) {
                    when (args[0].lowercase()) {
                        "start" -> tPlayer.startCampaign(args, sender)
                        "end" -> tPlayer.endCampaign(sender)
                        "invite" -> tPlayer.inviteCampaign(args, sender)
                        "delete" -> tPlayer.deleteCampaign(args, sender)
                    }
                } else {
                    getConsoleSender().sendMessage("Could not find this player")
                }
            }
        }

        return true
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
                            Bukkit.getLogger().info(campaigns.toString())
                            return campaigns.map { it.name }.toMutableList()
                        }
                    }
                }
            }
        }

        return null
    }

}
