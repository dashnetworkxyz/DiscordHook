package xyz.dashnetwork.discordhook.utils;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import xyz.dashnetwork.discordhook.DiscordHook;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class Configuration {

    private static final URL resource = Configuration.class.getClassLoader().getResource("config.yml");
    private static final File folder = DiscordHook.getDirectory().toFile();
    private static final File file = new File(folder, "config.yml");
    private static final ConfigurationOptions options = ConfigurationOptions.defaults().shouldCopyDefaults(true);
    private static ConfigurationNode config;

    public static void load() {
        assert resource != null;

        if (!folder.exists() && !folder.mkdirs())
            DiscordHook.getLogger().error("Failed to create plugin directory.");

        if (!file.exists()) {
            try {
                Files.copy(resource.openStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException exception) {
                exception.printStackTrace();
                return;
            }
        }

        YamlConfigurationLoader.Builder builder = YamlConfigurationLoader.builder();
        builder.defaultOptions(options);
        builder.file(file);

        YamlConfigurationLoader loader = builder.build();

        try {
            config = loader.load(options);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static String get(String node) { return config.node(node).getString(); }

}
