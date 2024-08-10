package org.padrewin.coldhudbar;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColdHudBar extends JavaPlugin implements Listener {
    private String actionBarMessage;
    private String pluginTag;
    private String noPermissionMessage;
    private String reloadSuccessMessage;
    private String version;
    private String developer;
    private String github;

    @Override
    public void onEnable() {

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("PlaceholderAPI is not loaded. This plugin won't work properly.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        String name = getDescription().getName();
        getLogger().info("");
        getLogger().info("  ____ ___  _     ____  ");
        getLogger().info(" / ___/ _ \\| |   |  _ \\ ");
        getLogger().info("| |  | | | | |   | | | |");
        getLogger().info("| |__| |_| | |___| |_| |");
        getLogger().info(" \\____\\___/|_____|____/");
        getLogger().info("    " + name + " v" + getDescription().getVersion());
        getLogger().info("    Author(s): " + getDescription().getAuthors().get(0));
        getLogger().info("    (c) Cold Development. All rights reserved.");
        getLogger().info("");

        saveDefaultConfig();

        loadConfig();

        getCommand("hudactionbar").setExecutor(this);

        getServer().getPluginManager().registerEvents(this, this);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    String message = PlaceholderAPI.setPlaceholders(player, actionBarMessage);
                    message = translateHexColorCodes(message);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            String message = PlaceholderAPI.setPlaceholders(player, actionBarMessage);
            message = translateHexColorCodes(message);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        } else {
            getLogger().warning("PlaceholderAPI is not loaded.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("hudactionbar")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("hudactionbar.reload")) {
                        reloadConfig();
                        loadConfig();
                        sender.sendMessage(formatMessage(pluginTag + " " + reloadSuccessMessage));
                    } else {
                        sender.sendMessage(formatMessage(pluginTag + " " + noPermissionMessage));
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("info")) {
                    sender.sendMessage(ChatColor.DARK_GRAY + "Version: v1.0");
                    sender.sendMessage(ChatColor.DARK_GRAY + "Developer: padrewin");
                    sender.sendMessage(ChatColor.DARK_GRAY + "GitHub: https://github.com/padrewin");
                    return true;
                }
            }
        }
        return false;
    }

    private void loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        pluginTag = config.getString("pluginTag");
        actionBarMessage = config.getString("actionBarMessage");
        noPermissionMessage = config.getString("messages.noPermission");
        reloadSuccessMessage = config.getString("messages.reloadSuccess");
        version = config.getString("info.version");
        developer = config.getString("info.developer");
        github = config.getString("info.github");

        actionBarMessage = translateHexColorCodes(actionBarMessage);
    }

    private String formatMessage(String message) {
        message = translateHexColorCodes(message);
        return ChatColor.translateAlternateColorCodes('&', message);
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
