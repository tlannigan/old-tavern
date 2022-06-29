package com.tlannigan.tavern.commands.gamemaster

import com.tlannigan.tavern.commands.gamemaster.Delete.Companion.deleteCommand
import com.tlannigan.tavern.commands.gamemaster.End.Companion.endCommand
import com.tlannigan.tavern.commands.gamemaster.Invite.Companion.inviteCommand
import com.tlannigan.tavern.commands.gamemaster.Kick.Companion.kickCommand
import com.tlannigan.tavern.commands.gamemaster.SetSpawn.Companion.setSpawnCommand
import com.tlannigan.tavern.commands.gamemaster.Spawn.spawnCommand
import com.tlannigan.tavern.commands.gamemaster.Start.Companion.startCommand
import dev.jorel.commandapi.CommandAPICommand

class GameMaster {

    companion object {

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

}