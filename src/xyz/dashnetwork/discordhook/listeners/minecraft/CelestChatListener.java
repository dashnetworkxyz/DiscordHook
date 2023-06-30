package xyz.dashnetwork.discordhook.listeners.minecraft;

import com.velocitypowered.api.event.Subscribe;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import xyz.dashnetwork.celest.events.CelestChatEvent;
import xyz.dashnetwork.celest.utils.chat.ColorUtils;
import xyz.dashnetwork.celest.utils.connection.User;
import xyz.dashnetwork.celest.utils.profile.NamedSource;
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
        String message = ColorUtils.strip(event.message());

        WebhookUtils.broadcast(channel, name, avatar, message);
    }

}
