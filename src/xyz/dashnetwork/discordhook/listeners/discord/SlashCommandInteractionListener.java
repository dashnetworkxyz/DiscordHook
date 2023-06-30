package xyz.dashnetwork.discordhook.listeners.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.dashnetwork.discordhook.command.DiscordCommand;

public final class SlashCommandInteractionListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild())
            return;

        String label = event.getName();
        DiscordCommand command = DiscordCommand.getInstance(label);

        if (command == null)
            return;

        event.deferReply(true).onSuccess(hook ->
            command.execute(event.getMember(), hook)
        ).queue();
    }

}
