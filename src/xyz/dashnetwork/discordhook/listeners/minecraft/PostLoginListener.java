package xyz.dashnetwork.discordhook.listeners.minecraft;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import xyz.dashnetwork.celest.utils.chat.ColorUtils;
import xyz.dashnetwork.celest.utils.connection.User;
import xyz.dashnetwork.celest.utils.storage.data.UserData;
import xyz.dashnetwork.discordhook.utils.ChannelList;

public final class PostLoginListener {

    @Subscribe(order = PostOrder.EARLY) // Run before Celest
    public void onPostLogin(PostLoginEvent event) {
        Player player = event.getPlayer();
        User user = User.getUser(player);
        UserData data = user.getData();
        EmbedBuilder builder = new EmbedBuilder();

        String displayname = ColorUtils.strip(user.getDisplayname());
        String avatar = "https://minotar.net/avatar/" + player.getUniqueId() + "/128";

        if (data.getVanish()) {
            builder.setColor(0x5555FF);
            builder.setAuthor(displayname + " silently joined.", null, avatar);

            ChannelList.STAFF_CHANNEL.sendMessageEmbeds(builder.build()).queue();
            return;
        } else {
            builder.setColor(0x55FF55);
            builder.setAuthor(displayname + " joined.", null, avatar);
        }

        ChannelList.GLOBAL_CHANNEL.sendMessageEmbeds(builder.build()).queue();
    }

}
