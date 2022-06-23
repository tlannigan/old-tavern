package com.tlannigan.tavern.commands

import com.tlannigan.tavern.utils.getTPlayer
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
//        val campaigns = CampaignRepository().findMany()
//        return campaigns.map { it.name }.toMutableList()
        return mutableListOf()
    }

}
