package com.github.derpynewbie.report.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum PluginConfig {
    NULL("config"),
    LATEST_REPORT_SIZE("config.latest-report-size"),
    COOL_DOWN_TIME_FORMAT("config.command-cool-down.time-format"),
    COOL_DOWN_TIME("config.command-cool-down.cool-down-time"),
    REPORT_ALIAS("config.command.report.alias-in-string-list"),
    SHOW_REPORT_ALIAS("config.command.show-reports.alias-in-string-list"),
    TOGGLE_REPORT_ALIAS("config.command.toggle-reports.alias-in-string-list"),
    ;

    private static boolean IS_INIT = false;
    private static FileConfiguration CONFIG;
    private String path;

    PluginConfig(String path) {
        this.path = path;
    }

    public static void reloadConfig(JavaPlugin pl) {
        pl.getLogger().info("Reloading config.");
        pl.saveDefaultConfig();
        pl.reloadConfig();
        CONFIG = pl.getConfig();
        IS_INIT = true;
        pl.getLogger().info("Reload complete.");
    }

    public String getPath() {
        return path;
    }

    public static boolean isInitialized() {
        return IS_INIT;
    }

    public String getString() {
        return CONFIG.getString(path);
    }

    @NotNull
    public List<String> getStringList() {
        return CONFIG.getStringList(path);
    }

    @NotNull
    public Double getDouble() {
        return CONFIG.getDouble(path);
    }

    public int getInt() {
        return CONFIG.getInt(path);
    }
}
