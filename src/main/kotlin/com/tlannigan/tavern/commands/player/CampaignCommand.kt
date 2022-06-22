package com.tlannigan.tavern.commands.player

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class CampaignCommand : TabExecutor {

    override fun onCommand(
        sender: CommandSender, command: Command, label: String, args: Array<out String>
    ): Boolean {
        if (sender is Player) {
            sender.sendMessage("You used the campaign command")
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String>? {
        // TODO("Not yet implemented")
        return mutableListOf("arg1", "arg2", "arg3")
    }

}