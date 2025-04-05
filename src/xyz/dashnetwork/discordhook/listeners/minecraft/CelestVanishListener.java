package xyz.dashnetwork.discordhook.listeners.minecraft;

import com.velocitypowered.api.event.Subscribe;
import net.dv8tion.jda.api.EmbedBuilder;
import xyz.dashnetwork.celest.chat.ColorUtils;
import xyz.dashnetwork.celest.connection.User;
import xyz.dashnetwork.celest.events.CelestVanishEvent;
import xyz.dashnetwork.discordhook.utils.ChannelList;

public final class CelestVanishListener {

    @Subscribe
    public void onCelestVanish(CelestVanishEvent event) {
        User user = event.user();
        boolean vanish = event.vanish();
        String displayname = ColorUtils.strip(user.getDisplayname());
        String avatar = "https://minotar.net/avatar/" + user.getUuid() + "/128";

        EmbedBuilder builder = new EmbedBuilder();

        if (vanish) {
            builder.setColor(0xFF5555);
            builder.setAuthor(displayname + " left.", null, avatar);
        } else {
            builder.setColor(0x55FF55);
            builder.setAuthor(displayname + " joined.", null, avatar);
        }

        ChannelList.GLOBAL_CHANNEL.sendMessageEmbeds(builder.build()).queue();

        builder = new EmbedBuilder();
        builder.setColor(0x5555FF);

        if (vanish)
            builder.setAuthor(displayname + " is now vanished. Poof.", null, avatar);
        else
            builder.setAuthor(displayname + " is no longer vanished.", null, avatar);

        ChannelList.STAFF_CHANNEL.sendMessageEmbeds(builder.build()).queue();
    }

}
