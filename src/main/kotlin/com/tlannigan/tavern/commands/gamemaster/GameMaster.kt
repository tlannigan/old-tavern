package com.tlannigan.tavern.commands.gamemaster

import com.tlannigan.tavern.commands.gamemaster.Delete.deleteCommand
import com.tlannigan.tavern.commands.gamemaster.End.endCommand
import com.tlannigan.tavern.commands.gamemaster.Invite.inviteCommand
import com.tlannigan.tavern.commands.gamemaster.Kick.kickCommand
import com.tlannigan.tavern.commands.gamemaster.SetSpawn.setSpawnCommand
import com.tlannigan.tavern.commands.gamemaster.Spawn.spawnCommand
import com.tlannigan.tavern.commands.gamemaster.Start.startCommand
import dev.jorel.commandapi.CommandAPICommand

object GameMaster {

    val gameMasterCommand: CommandAPICommand =
        CommandAPICommand("gamemaster")
            .withAliases("gm")
            .withSubcommand(startCommand)
            .withSubcommand(endCommand)
            .withSubcommand(inviteCommand)
            .withSubcommand(kickCommand)
            .withSubcommand(deleteCommand)
            .withSubcommand(spawnCommand)
            .withSubcommand(setSpawnCommand)

}
