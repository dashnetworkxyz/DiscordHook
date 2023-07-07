package xyz.dashnetwork.discordhook.listeners.discord;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import xyz.dashnetwork.celest.utils.StringUtils;
import xyz.dashnetwork.celest.utils.chat.ColorUtils;
import xyz.dashnetwork.celest.utils.chat.MessageUtils;
import xyz.dashnetwork.celest.utils.chat.builder.MessageBuilder;
import xyz.dashnetwork.celest.utils.chat.builder.TextSection;
import xyz.dashnetwork.celest.utils.connection.User;
import xyz.dashnetwork.discordhook.DiscordHook;
import xyz.dashnetwork.discordhook.utils.ChannelList;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MessageReceivedListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isWebhookMessage() || !event.isFromGuild() || event.getAuthor().isBot())
            return;

        GuildMessageChannelUnion channel = event.getGuildChannel();

        if (!DiscordHook.getGuild().getId().equals(channel.getGuild().getId()))
            return;

        Message message = event.getMessage();
        String content = message.getContentStripped();

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
        String nearest = ColorUtils.fromNamedTextColor(named);
        String nickname = member.getNickname();
        String username = author.getName();
        String id = author.getId();
        MessageBuilder builder = new MessageBuilder();

        if (nickname == null)
            nickname = author.getGlobalName();
        if (nickname == null)
            nickname = username;

        if (staff) builder.append("&9&lStaff&r ");
        else if (admin) builder.append("&9&lAdmin&r ");
        else if (owner) builder.append("&9&lOwner&r ");

        builder.append("&9&lDiscord").hover("&6" + username).insertion(id);
        builder.append("&f " + nearest + nickname).hover("&6" + username).insertion(id);

        if (global) builder.append("&r &l»&r");
        else if (staff) builder.append("&r &6&l»&6");
        else if (admin) builder.append("&r &3&l»&3");
        else builder.append("&r &c&l»&c");

        for (String split : content.split(" ")) {
            if (split.length() > 0) {
                TextSection section = builder.append(" " + split);

                if (StringUtils.matchesUrl(split)) {
                    String url = split.toLowerCase().startsWith("http") ? split : "https://" + split;

                    section.hover("&7Click to open &6" + url).click(ClickEvent.openUrl(url));
                }
            }
        }

        for (Message.Attachment attachment : message.getAttachments()) {
            String url = attachment.getUrl();

            builder.append(" ");
            builder.append("&7&o(attachment)")
                    .hover("&7Click to open &6" + url)
                    .click(ClickEvent.openUrl(url));
        }

        if (global) MessageUtils.broadcast(builder::build);
        else if (staff) MessageUtils.broadcast(User::isStaff, builder::build);
        else if (admin) MessageUtils.broadcast(User::isAdmin, builder::build);
        else MessageUtils.broadcast(User::isOwner, builder::build);
    }

}
