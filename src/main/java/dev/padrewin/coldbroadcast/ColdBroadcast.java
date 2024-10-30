package dev.padrewin.coldbroadcast;

import dev.padrewin.coldbroadcast.manager.CommandManager;
import dev.padrewin.coldbroadcast.manager.LocaleManager;
import dev.padrewin.coldbroadcast.setting.SettingKey;
import dev.padrewin.colddev.ColdPlugin;
import dev.padrewin.colddev.config.ColdSetting;
import dev.padrewin.colddev.manager.Manager;
import dev.padrewin.colddev.manager.PluginUpdateManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColdBroadcast extends ColdPlugin implements Listener {

    /**
     * Console colors
     */
    String ANSI_RESET = "\u001B[0m";
    String ANSI_GREEN = "\u001B[32m";
    String ANSI_YELLOW = "\u001B[33m";
    String ANSI_RED = "\u001B[31m";
    String ANSI_PURPLE = "\u001B[35m";
    String ANSI_AQUA = "\u001B[36m";

    private static ColdBroadcast instance;
    private String actionBarMessage;

    public ColdBroadcast() {
        super("Cold-Development", "ColdBroadcast", 23655, null, LocaleManager.class, null);
        instance = this;
    }

    @Override
    public void enable() {
        instance = this;
        getManager(PluginUpdateManager.class);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("PlaceholderAPI is not loaded. This plugin won't work properly.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        String name = getDescription().getName();
        getLogger().info(ANSI_PURPLE + "  ____ ___  _     ____  " + ANSI_RESET);
        getLogger().info(ANSI_AQUA + " / ___/ _ \\| |   |  _ \\ " + ANSI_RESET);
        getLogger().info(ANSI_PURPLE + "| |  | | | | |   | | | |" + ANSI_RESET);
        getLogger().info(ANSI_AQUA + "| |__| |_| | |___| |_| |" + ANSI_RESET);
        getLogger().info(ANSI_PURPLE + " \\____\\___/|_____|____/ " + ANSI_RESET);
        getLogger().info("    " + ANSI_GREEN + name + ANSI_RED + " v" + getDescription().getVersion() + ANSI_RESET);
        getLogger().info(ANSI_YELLOW + "    Author(s): " + ANSI_YELLOW + getDescription().getAuthors().get(0) + ANSI_RESET);
        getLogger().info(ANSI_AQUA + "    (c) Cold Development ❄" + ANSI_RESET);
        getLogger().info("");

        File configFile = new File(getDataFolder(), "en_US.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }

        saveDefaultConfig();
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (actionBarMessage == null) {
                    return;
                }

                for (Player player : getServer().getOnlinePlayers()) {
                    String message = PlaceholderAPI.setPlaceholders(player, actionBarMessage);
                    message = translateHexColorCodes(message);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    @Override
    public void disable() {
        getLogger().info("ColdBroadcast disabled.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (actionBarMessage == null) {
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            String message = PlaceholderAPI.setPlaceholders(player, actionBarMessage);
            message = translateHexColorCodes(message);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        } else {
            getLogger().warning("PlaceholderAPI is not loaded.");
        }
    }

    public void loadConfig() {
        boolean isActionBarEnabled = getConfig().getBoolean("enableActionBarMessage", true);

        if (isActionBarEnabled) {
            actionBarMessage = getConfig().getString("actionBarMessage", "&#635AA7g&#6D63ADi&#776DB3t&#8176BAh&#8B80C0u&#9589C6b&#9F92CC.&#AA9CD3c&#B4A5D9o&#BEAEDFm&#C8B8E5/&#D2C1ECC&#DCCBF2o&#E6D4F8l&#E0C9F8d&#DBBEF8-&#D5B4F8D&#D0A9F7e&#CA9EF7v&#C593F7e&#BF89F7l&#BA7EF7o&#B473F7p&#AF68F6m&#A95EF6e&#A453F6n&#9E48F6t");
            if (actionBarMessage != null) {
                actionBarMessage = translateHexColorCodes(actionBarMessage);
            } else {
                getLogger().warning("actionBarMessage is null. Please check the configuration in config.yml.");
            }
        } else {
            actionBarMessage = null;
            getLogger().info("Action bar message is disabled in the config.");
        }
    }

    @Override
    protected @NotNull List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(
                CommandManager.class
        );
    }

    @Override
    protected @NotNull List<ColdSetting<?>> getColdConfigSettings() {
        return SettingKey.getKeys();
    }

    @Override
    protected String[] getColdConfigHeader() {
        return new String[] {
                "  ____  ___   _      ____   ",
                " / ___|/ _ \\ | |    |  _ \\  ",
                "| |   | | | || |    | | | | ",
                "| |___| |_| || |___ | |_| | ",
                " \\____|\\___/ |_____|_____/  ",
                "                           "
        };
    }

    public static ColdBroadcast getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ColdBroadcast instance is not initialized!");
        }
        return instance;
    }

    private String translateHexColorCodes(String message) {
        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length());

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            String replacement = ChatColor.of("#" + hexColor).toString();
            matcher.appendReplacement(buffer, replacement);
        }

        matcher.appendTail(buffer);
        message = buffer.toString();

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
