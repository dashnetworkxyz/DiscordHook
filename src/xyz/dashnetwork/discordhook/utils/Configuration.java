package xyz.dashnetwork.discordhook.utils;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
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
    private static final ConfigurationOptions options = ConfigurationOptions.defaults().withShouldCopyDefaults(true);
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

        YAMLConfigurationLoader.Builder builder = YAMLConfigurationLoader.builder();
        builder.setDefaultOptions(options);
        builder.setFile(file);

        ConfigurationLoader<ConfigurationNode> loader = builder.build();

        try {
            config = loader.load(options);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static String get(String node) { return config.getNode(node).getString(); }

}
