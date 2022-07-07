package com.tlannigan.tavern.commands

import com.tlannigan.tavern.commands.campaign.Create.createCommand
import com.tlannigan.tavern.commands.campaign.Enter.enterCommand
import com.tlannigan.tavern.commands.campaign.Join.joinCommand
import com.tlannigan.tavern.commands.campaign.Leave.leaveCommand
import dev.jorel.commandapi.CommandAPICommand

object Campaign {

    val campaignCommand: CommandAPICommand =
        CommandAPICommand("campaign")
            .withAliases("c")
            .withSubcommand(createCommand)
            .withSubcommand(enterCommand)
            .withSubcommand(leaveCommand)
            .withSubcommand(joinCommand)

}
