package com.tlannigan.tavern.utils

import com.tlannigan.tavern.models.TCampaign
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor

object ChatComponents {

    fun inviteCampaign(tCampaign: TCampaign): TextComponent {
        return Component.text("You have been invited to ")
            .append(
                Component.text("[JOIN]")
                    .color(NamedTextColor.YELLOW)
                    .clickEvent(ClickEvent.runCommand("/join ${tCampaign.id}")))
            .append(Component.text(" ${tCampaign.name}"))
    }

}
