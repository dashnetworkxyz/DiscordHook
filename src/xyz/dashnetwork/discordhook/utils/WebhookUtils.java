package xyz.dashnetwork.discordhook.utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WebhookUtils {

    private static final Map<Long, Webhook> webhookMap = new HashMap<>();

    public static void broadcast(TextChannel channel, String name, String avatar, String message) {
        Webhook webhook = getWebhook(channel);

        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setUsername(name);
        builder.setAvatarUrl(avatar);
        builder.setContent(message);

        WebhookClient client = WebhookClientBuilder.fromJDA(webhook).build();
        client.send(builder.build());
        client.close();
    }

    private static Webhook getWebhook(TextChannel channel) {
        return webhookMap.computeIfAbsent(channel.getIdLong(), id -> {
            List<Webhook> webhooks = channel.getGuild().retrieveWebhooks().complete();

            for (Webhook webhook : webhooks)
                if (webhook.getChannel().equals(channel) && webhook.getName().equals("Minecraft-Hook-" + id))
                    return webhook;

            return channel.createWebhook("Minecraft-Hook-" + id).complete();
        });
    }

}
