package xyz.dashnetwork.discordhook.command.commands;

import com.velocitypowered.api.proxy.ServerConnection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import xyz.dashnetwork.celest.utils.ListUtils;
import xyz.dashnetwork.celest.utils.chat.ColorUtils;
import xyz.dashnetwork.celest.utils.connection.User;
import xyz.dashnetwork.discordhook.command.DiscordCommand;
import xyz.dashnetwork.discordhook.utils.RoleList;

import java.util.*;
import java.util.stream.Collectors;

public final class CommandList extends DiscordCommand {

    public CommandList() { super("list", "Lists all online players"); }

    @Override
    public void execute(Member member, InteractionHook hook) {
        Map<String, List<String>> map = new TreeMap<>(String::compareTo);
        int size = 0;

        for (User each : User.getUsers()) {
            if (!each.getData().getVanish() || RoleList.isStaff(member)) {
                Optional<ServerConnection> optional = each.getPlayer().getCurrentServer();

                if (optional.isEmpty())
                    continue;

                String name = optional.get().getServerInfo().getName();
                List<String> list = map.getOrDefault(name, new ArrayList<>());

                list.add(ColorUtils.strip(each.getDisplayname()));
                map.put(name, list);
                size++;
            }
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Global List");
        builder.setDescription(size + " players online");
        builder.setColor(0xFFAA00);

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String players = String.join(", ", entry.getValue());
            builder.addField(entry.getKey(), players, false);
        }

        hook.editOriginalEmbeds(builder.build()).queue();
    }

}
