package xyz.dashnetwork.discordhook.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import xyz.dashnetwork.discordhook.DiscordHook;

import java.util.ArrayList;
import java.util.List;

public abstract class DiscordCommand {

    private static final List<DiscordCommand> commands = new ArrayList<>();
    private static final JDA jda = DiscordHook.getJDA();
    private final String label;

    public DiscordCommand(String label, String description) {
        this.label = label;

        jda.upsertCommand(label, description).queue();
        commands.add(this);
    }

    public static DiscordCommand getInstance(String label) {
        for (DiscordCommand command : commands)
            if (command.label.equals(label))
                return command;
        return null;
    }

    public abstract void execute(Member member, InteractionHook hook);

}
