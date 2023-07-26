package xyz.dashnetwork.discordhook;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import xyz.dashnetwork.discordhook.command.commands.CommandList;
import xyz.dashnetwork.discordhook.listeners.discord.MessageReceivedListener;
import xyz.dashnetwork.discordhook.listeners.discord.SlashCommandInteractionListener;
import xyz.dashnetwork.discordhook.listeners.minecraft.CelestChatListener;
import xyz.dashnetwork.discordhook.listeners.minecraft.CelestVanishListener;
import xyz.dashnetwork.discordhook.listeners.minecraft.DisconnectListener;
import xyz.dashnetwork.discordhook.listeners.minecraft.PostLoginListener;
import xyz.dashnetwork.discordhook.utils.ChannelList;
import xyz.dashnetwork.discordhook.utils.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "discordhook",
        name = "DiscordHook",
        version = "1.0.0",
        authors = {"MasterDash5"},
        dependencies = {
                @Dependency(id = "celest")
        }
)
public final class DiscordHook {

    private static ProxyServer server;
    private static Logger logger;
    private static Path directory;
    private static JDA jda;
    private static Guild guild;

    public static Logger getLogger() { return logger; }

    public static Path getDirectory() { return directory; }

    public static JDA getJDA() { return jda; }

    public static Guild getGuild() { return guild; }

    @Inject
    public DiscordHook(ProxyServer server, Logger logger, @DataDirectory Path directory) {
        DiscordHook.server = server;
        DiscordHook.logger = logger;
        DiscordHook.directory = directory;

        Configuration.load();

        File file = new File(directory.toFile(), "bot.token");

        if (!file.exists()) {
            logger.error("No bot.token found in plugin folder.");
            return;
        }

        String token;

        try {
            FileInputStream input = new FileInputStream(file);
            token = new String(input.readAllBytes());

            input.close();
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }

         jda = JDABuilder.createLight(token)
                 .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                 .setActivity(Activity.playing("play.dashnetwork.xyz"))
                 .setStatus(OnlineStatus.IDLE)
                 .build();
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        guild = jda.getGuildById(Configuration.get("guild"));

        EventManager eventManager = server.getEventManager();
        eventManager.register(this, new CelestChatListener());
        eventManager.register(this, new CelestVanishListener());
        eventManager.register(this, new DisconnectListener());
        eventManager.register(this, new PostLoginListener());

        new CommandList();

        jda.addEventListener(new MessageReceivedListener());
        jda.addEventListener(new SlashCommandInteractionListener());
        jda.getPresence().setStatus(OnlineStatus.ONLINE);

        ChannelList.GLOBAL_CHANNEL.sendMessage("```diff\n+ Server has started\n```").queue();
    }

    @Subscribe(async = false)
    public void onProxyShutdown(ProxyShutdownEvent event) {
        ChannelList.GLOBAL_CHANNEL.sendMessage("```diff\n- Server has stopped\n```").queue();

        jda.shutdown();
    }

}
