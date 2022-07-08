package com.tlannigan.tavern.utils

import com.tlannigan.tavern.models.TCampaign
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.event.HoverEventSource
import net.kyori.adventure.text.format.NamedTextColor

object ChatComponents {

    fun inviteCampaign(tCampaign: TCampaign): TextComponent {
        return Component.text("${tCampaign.gameMaster.name} has invited you to ")
            .append(
                Component.text("[join]")
                    .color(NamedTextColor.YELLOW)
                    .hoverEvent(HoverEventSource {
                        HoverEvent.showText(Component.text("Click to join ${tCampaign.name}"))
                    })
                    .clickEvent(
                        ClickEvent.runCommand("/campaign join ${tCampaign.id}")
                    )
            )
            .append(Component.text(" ${tCampaign.name}"))
    }

}
