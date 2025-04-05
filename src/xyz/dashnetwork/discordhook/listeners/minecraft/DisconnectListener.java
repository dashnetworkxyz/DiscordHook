package xyz.dashnetwork.discordhook.listeners.minecraft;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import xyz.dashnetwork.celest.chat.ColorUtils;
import xyz.dashnetwork.celest.connection.User;
import xyz.dashnetwork.celest.utils.LazyUtils;
import xyz.dashnetwork.discordhook.utils.ChannelList;

public final class DisconnectListener {

    @Subscribe(order = PostOrder.EARLY) // Run before Celest
    public void onDisconnect(DisconnectEvent event) {
        if (LazyUtils.anyEquals(event.getLoginStatus(),
                DisconnectEvent.LoginStatus.PRE_SERVER_JOIN,
                DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN)) {
            User user = User.getUser(event.getPlayer());
            String displayname = ColorUtils.strip(user.getDisplayname());
            String avatar = "https://minotar.net/avatar/" + user.getUuid() + "/128";

            EmbedBuilder builder = new EmbedBuilder();

            if (user.getData().getVanish()) {
                builder.setColor(0x5555FF);
                builder.setAuthor(displayname + " silently left.", null, avatar);

                ChannelList.STAFF_CHANNEL.sendMessageEmbeds(builder.build()).queue();
            } else {
                builder.setColor(0xFF5555);
                builder.setAuthor(displayname + " left.", null, avatar);

                ChannelList.GLOBAL_CHANNEL.sendMessageEmbeds(builder.build()).queue();
            }
        }
    }

}
