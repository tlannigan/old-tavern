package com.tlannigan.tavern.commands.player

import com.tlannigan.tavern.commands.player.Create.Companion.createCommand
import com.tlannigan.tavern.commands.player.Enter.Companion.enterCommand
import com.tlannigan.tavern.commands.player.Leave.Companion.leaveCommand
import dev.jorel.commandapi.CommandAPICommand

class Campaign {

    companion object {

        val campaignCommand = CommandAPICommand("campaign")
            .withAliases("c")
            .withSubcommand(createCommand)
            .withSubcommand(enterCommand)
            .withSubcommand(leaveCommand)

    }

}