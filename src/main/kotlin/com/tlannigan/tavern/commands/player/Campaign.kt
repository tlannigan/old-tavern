package com.tlannigan.tavern.commands.player

import com.tlannigan.tavern.commands.player.Create.createCommand
import com.tlannigan.tavern.commands.player.Enter.enterCommand
import com.tlannigan.tavern.commands.player.Leave.leaveCommand
import dev.jorel.commandapi.CommandAPICommand

object Campaign {

    val campaignCommand = CommandAPICommand("campaign")
        .withAliases("c")
        .withSubcommand(createCommand)
        .withSubcommand(enterCommand)
        .withSubcommand(leaveCommand)

}
