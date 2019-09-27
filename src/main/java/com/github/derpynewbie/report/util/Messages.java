package com.github.derpynewbie.report.util;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public enum Messages {
    REPORT_USAGE("message.report.usage"),
    REPORT_NO_CONSOLE("message.report.no-console"),
    REPORT_NO_PLAYER_NAME_PROVIDED("message.report.no-player-provided"),
    REPORT_NO_REASON_PROVIDED("message.report.no-reason-provided"),
    REPORT_NO_COMMAND_WHILE_IN_COOL_DOWN("message.report.no-command-in-cooldown"),
    REPORT_PLAYER_NOT_ONLINE("message.report.player-not-online"),
    REPORT_SUCCESS("message.report.report-success"),
    SHOW_REPORT_USAGE("message.show-report.usage"),
    SHOW_REPORT_WRONG_SYNTAX("message.show-report.wrong-syntax"),
    SHOW_REPORT_NO_INTEGER_PROVIDED("message.show-report.no-integer-provided"),
    SHOW_REPORT_NOT_ENOUGH_ARGUMENT("message.show-report.not-enough-argument"),
    SHOW_REPORT_LATEST("message.show-report.show-latest"),
    SHOW_REPORT_ON_DATA_LOAD("message.show-report.on-data-load"),
    SHOW_REPORT_NO_SUCH_DATA_FOUND("message.show-report.no-such-data-found"),
    SHOW_REPORT_NO_DATA_FOUND("message.show-report.no-data-found"),
    SHOW_REPORT_NO_DATA_FOUND_WITH_FIRST_ARGUMENT("message.show-report.no-data-found-with-first-argument"),
    SHOW_REPORT_DATA_BEGIN("message.show-report.report-data-begin"),
    SHOW_REPORT_DATA_FORMAT("message.show-report.report-data-format"),
    SHOW_REPORT_DATA_END("message.show-report.report-data-end"),
    SHOW_REPORT_REMOVE_SUCCESS("message.show-report.remove-success"),
    ;

    private static FileConfiguration CONFIG_FILE;
    private static File MESSAGE_FILE;

    private String path;

    Messages(String path) {
        this.path = path;
    }

    public static void reloadMessage(JavaPlugin pl) {
        pl.getLogger().info("Reloading message.");
        MESSAGE_FILE = new File(pl.getDataFolder(), "message.yml");
        if (!MESSAGE_FILE.exists())
            pl.saveResource("message.yml", false);
        CONFIG_FILE = YamlConfiguration.loadConfiguration(MESSAGE_FILE);
        pl.getLogger().info("Reload complete.");
    }

    @NotNull
    public static String replacePlaceholder(@NotNull String msg, @Nullable Map<String, String> placeholderData) {
        if (placeholderData != null) {
            for (Map.Entry<String, String> data :
                    placeholderData.entrySet()) {
                msg = msg.replaceAll(data.getKey(), data.getValue());
            }
        }
        return msg;
    }

    @NotNull
    public static Map<String, String> getPlayerPlaceholder(@NotNull OfflinePlayer p) {
        return getPlayerPlaceholder(p, null);
    }

    @NotNull
    public static Map<String, String> getPlayerPlaceholder(@NotNull OfflinePlayer p, @Nullable String prefix) {
        HashMap<String, String> map = new HashMap<>();
        Player onlinePlayer = p.getPlayer();

        map.put(prefix == null || prefix.isEmpty() ? "\\{NAME\\}" : "\\{" + prefix + "_NAME\\}", p.getName());
        map.put(prefix == null || prefix.isEmpty() ? "\\{DISPLAY_NAME\\}" : "\\{" + prefix + "_DISPLAY_NAME\\}", onlinePlayer == null ? p.getName() : onlinePlayer.getDisplayName());

        return map;
    }

    @NotNull
    public static Map<String, String> getCommandSenderPlaceholder(@NotNull CommandSender p) {
        return getCommandSenderPlaceholder(p, null);
    }

    @NotNull
    public static Map<String, String> getCommandSenderPlaceholder(@NotNull CommandSender p, @Nullable String prefix) {
        if (p instanceof OfflinePlayer)
            return getPlayerPlaceholder((OfflinePlayer) p, prefix);
        else {
            HashMap<String, String> map = new HashMap<>();

            map.put(prefix == null || prefix.isEmpty() ? "\\{NAME\\}" : "\\{" + prefix + "_NAME\\}", p.getName());
            map.put(prefix == null || prefix.isEmpty() ? "\\{DISPLAY_NAME\\}" : "\\{" + prefix + "_DISPLAY_NAME\\}", p.getName());

            return map;
        }
    }

    @NotNull
    public String getMessage() {
        String msg = CONFIG_FILE.getString(path);
        return ChatColor.translateAlternateColorCodes('&', msg == null ? "" : msg);
    }

    public boolean sendMessageIfExists(Player p, Map<String, String> placeholder) {
        return sendMessageIfExists((CommandSender) p, placeholder);
    }

    public boolean sendMessageIfExists(CommandSender s, Map<String, String> placeholder) {
        String msg = getMessage();
        if (!msg.isEmpty()) {
            s.sendMessage(replacePlaceholder(msg, placeholder));
            return true;
        }
        return false;
    }
}
