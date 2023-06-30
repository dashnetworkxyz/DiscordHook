package xyz.dashnetwork.discordhook.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import xyz.dashnetwork.discordhook.DiscordHook;

public final class RoleList {

    private static final Guild guild = DiscordHook.getGuild();
    public static Role STAFF_ROLE;
    public static Role OWNER_ROLE;

    static {
        STAFF_ROLE = guild.getRoleById(Configuration.get("staff-role"));
        OWNER_ROLE = guild.getRoleById(Configuration.get("owner-role"));
    }

    public static boolean isStaff(Member member) {
        return member.getRoles().stream().anyMatch(role ->
                role.equals(STAFF_ROLE) || role.equals(OWNER_ROLE)
        );
    }

}
