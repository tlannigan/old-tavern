package com.tlannigan.tavern.commands.player

import com.tlannigan.tavern.utils.getTPlayer
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
                        "create" -> tPlayer.createCampaign(args, sender)
                        "enter" -> tPlayer.enterCampaign(args, sender)
                        "leave" -> tPlayer.leaveCampaign(sender)
                    }
                } else {
                    Bukkit.getConsoleSender().sendMessage("Could not find this player")
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
