package xyz.dashnetwork.discordhook.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import xyz.dashnetwork.discordhook.DiscordHook;

public final class ChannelList {

    private static final Guild guild = DiscordHook.getGuild();
    public static TextChannel GLOBAL_CHANNEL;
    public static TextChannel STAFF_CHANNEL;
    public static TextChannel ADMIN_CHANNEL;
    public static TextChannel OWNER_CHANNEL;

    static {
        GLOBAL_CHANNEL = guild.getTextChannelById(Configuration.get("global-channel"));
        STAFF_CHANNEL = guild.getTextChannelById(Configuration.get("staff-channel"));
        ADMIN_CHANNEL = guild.getTextChannelById(Configuration.get("admin-channel"));
        OWNER_CHANNEL = guild.getTextChannelById(Configuration.get("owner-channel"));
    }

    public static boolean isGlobal(Channel channel) { return channel.getId().equals(GLOBAL_CHANNEL.getId()); }

    public static boolean isStaff(Channel channel) { return channel.getId().equals(STAFF_CHANNEL.getId()); }

    public static boolean isAdmin(Channel channel) { return channel.getId().equals(ADMIN_CHANNEL.getId()); }

    public static boolean isOwner(Channel channel) { return channel.getId().equals(OWNER_CHANNEL.getId()); }

}
