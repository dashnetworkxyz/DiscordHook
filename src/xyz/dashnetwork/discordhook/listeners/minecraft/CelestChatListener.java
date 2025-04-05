package xyz.dashnetwork.discordhook.listeners.minecraft;

import com.velocitypowered.api.event.Subscribe;
import dev.vankka.mcdiscordreserializer.discord.DiscordSerializer;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import xyz.dashnetwork.celest.chat.ColorUtils;
import xyz.dashnetwork.celest.chat.ComponentUtils;
import xyz.dashnetwork.celest.connection.User;
import xyz.dashnetwork.celest.events.CelestChatEvent;
import xyz.dashnetwork.celest.profile.NamedSource;
import xyz.dashnetwork.discordhook.DiscordHook;
import xyz.dashnetwork.discordhook.utils.ChannelList;
import xyz.dashnetwork.discordhook.utils.WebhookUtils;

public final class CelestChatListener {

    @Subscribe
    public void onCelestChat(CelestChatEvent event) {
        TextChannel channel;

        switch (event.channel()) {
            case GLOBAL -> channel = ChannelList.GLOBAL_CHANNEL;
            case STAFF -> channel = ChannelList.STAFF_CHANNEL;
            case ADMIN -> channel = ChannelList.ADMIN_CHANNEL;
            case OWNER -> channel = ChannelList.OWNER_CHANNEL;
            default -> { return; }
        }

        NamedSource named = event.named();
        String avatar = named instanceof User ?
                "https://minotar.net/avatar/" + ((User) named).getUuid() + "/128" :
                DiscordHook.getJDA().getSelfUser().getAvatarUrl();

        String name = ColorUtils.strip(named.getDisplayname());
        String message = DiscordSerializer.INSTANCE.serialize(ComponentUtils.fromString(event.message()));

        WebhookUtils.broadcast(channel, name, avatar, message);
    }

}
