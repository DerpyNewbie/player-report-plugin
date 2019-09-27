package com.github.derpynewbie.report.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public enum PluginConfig {
    LATEST_REPORT_SIZE("config.latest-report-size"),
    COOL_DOWN_TIME_FORMAT("config.command-cool-down.time-format"),
    COOL_DOWN_TIME("config.command-cool-down.cool-down-time"),
    ;

    private static FileConfiguration config;
    private String path;

    PluginConfig(String path) {
        this.path = path;
    }

    public static void reloadConfig(JavaPlugin pl) {
        pl.getLogger().info("Reloading config.");
        pl.saveDefaultConfig();
        pl.reloadConfig();
        config = pl.getConfig();
        pl.getLogger().info("Reload complete.");
    }

    public String getPath() {
        return path;
    }

    public String getString() {
        return config.getString(path);
    }

    public Double getDouble() {
        return config.getDouble(path);
    }

    public int getInt() {
        return config.getInt(path);
    }
}
