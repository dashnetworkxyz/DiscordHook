package xyz.dashnetwork.discordhook.listeners.discord;

import dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import xyz.dashnetwork.celest.utils.StringUtils;
import xyz.dashnetwork.celest.utils.chat.ColorUtils;
import xyz.dashnetwork.celest.utils.chat.ComponentUtils;
import xyz.dashnetwork.celest.utils.chat.builder.MessageBuilder;
import xyz.dashnetwork.celest.utils.chat.builder.Section;
import xyz.dashnetwork.celest.utils.connection.User;
import xyz.dashnetwork.discordhook.DiscordHook;
import xyz.dashnetwork.discordhook.utils.ChannelList;

import java.awt.*;
import java.util.Objects;

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

        if (staff) builder.append("&9&lStaff&f ");
        else if (admin) builder.append("&9&lAdmin&f ");
        else if (owner) builder.append("&9&lOwner&f ");

        builder.append("&9&lDiscord&f " + nearest + nickname).hover("&6" + username).insertion(id);

        if (global) builder.append("&f &l»&f");
        else if (staff) builder.append("&f &6&l»&6");
        else if (admin) builder.append("&f &3&l»&3");
        else builder.append("&f &c&l»&c");

        if (message.getType().equals(MessageType.INLINE_REPLY)) {
            Message referenced = Objects.requireNonNull(message.getMessageReference()).resolve().complete();
            Component reply = MinecraftSerializer.INSTANCE.serialize(referenced.getContentDisplay());

            builder.append(" ");
            builder.append("&7&o(reply)&f")
                    .hover("&6" + referenced.getAuthor().getName())
                    .hover("&7" + ColorUtils.strip(ComponentUtils.toString(reply)));
        }

        content = ColorUtils.strip(ComponentUtils.toString(MinecraftSerializer.INSTANCE.serialize(content)));

        for (String split : content.split(" ")) {
            if (split.length() > 0) {
                Section section = builder.append(" " + split);

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

        if (global) builder.broadcast();
        else if (staff) builder.broadcast(User::isStaff);
        else if (admin) builder.broadcast(User::isAdmin);
        else builder.broadcast(User::isOwner);
    }

}
