package xyz.dashnetwork.discordhook.listeners.discord;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import xyz.dashnetwork.celest.utils.chat.ColorUtils;
import xyz.dashnetwork.celest.utils.chat.MessageUtils;
import xyz.dashnetwork.celest.utils.chat.builder.MessageBuilder;
import xyz.dashnetwork.celest.utils.connection.User;
import xyz.dashnetwork.discordhook.DiscordHook;
import xyz.dashnetwork.discordhook.utils.ChannelList;

import java.awt.*;

public class MessageReceivedListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isWebhookMessage() || !event.isFromGuild() || event.getAuthor().isBot())
            return;

        GuildMessageChannelUnion channel = event.getGuildChannel();

        if (!DiscordHook.getGuild().getId().equals(channel.getGuild().getId()))
            return;

        String content = event.getMessage().getContentStripped();

        boolean global = ChannelList.isGlobal(channel);
        boolean staff = ChannelList.isStaff(channel);
        boolean admin = ChannelList.isAdmin(channel);
        boolean owner = ChannelList.isOwner(channel);

        if (!global && !staff && !admin && !owner)
            return;

        Member member = event.getMember();
        assert member != null;

        Color color = member.getColor();

        if (color == null)
            color = Color.GRAY;

        net.dv8tion.jda.api.entities.User author = event.getAuthor();
        NamedTextColor named = NamedTextColor.nearestTo(TextColor.color(color.getRGB()));
        String section = ColorUtils.fromNamedTextColor(named);
        String nickname = member.getNickname();
        String username = author.getName();
        String id = author.getId();
        MessageBuilder message = new MessageBuilder();

        if (nickname == null)
            nickname = author.getGlobalName();
        if (nickname == null)
            nickname = username;

        if (staff) message.append("&9&lStaff&r ");
        else if (admin) message.append("&9&lAdmin&r ");
        else if (owner) message.append("&9&lOwner&r ");

        message.append("&9&lDiscord").hover("&6" + username).insertion(id);
        message.append("&f " + section + nickname).hover("&6" + username).insertion(id);

        if (global) message.append("&r &l»&r ");
        else if (staff) message.append("&r &6&l»&6 ");
        else if (admin) message.append("&r &3&l»&3 ");
        else message.append("&r &c&l»&c ");

        message.append(content);

        if (global) MessageUtils.broadcast(message::build);
        else if (staff) MessageUtils.broadcast(User::isStaff, message::build);
        else if (admin) MessageUtils.broadcast(User::isAdmin, message::build);
        else MessageUtils.broadcast(User::isOwner, message::build);
    }

}
