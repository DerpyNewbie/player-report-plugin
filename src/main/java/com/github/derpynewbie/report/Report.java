package com.github.derpynewbie.report;

import com.github.derpynewbie.report.util.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Report extends JavaPlugin implements CommandExecutor, Listener {

    private static Set<Player> notificationDisabledPlayerSet = new HashSet<>();

    private static Report instance;
    private ReportHelper repHelper;

    public static Report getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        repHelper.saveDataConfig();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        instance = this;
        repHelper = new ReportHelper(this);

        PluginConfig.reloadConfig(this);
        Messages.reloadMessage(this);
        Commands.reloadCommands(this);

        Runnable autoSaveTask = () -> repHelper.saveDataConfig();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, autoSaveTask, 20 * 60, 20 * 60 * 5);

    }

    public void setupConfig() {
        saveDefaultConfig();
        reloadConfig();
    }

    public ReportHelper getHelper() {
        return repHelper;
    }

    public static Set<Player> getNotificationDisabledPlayerSet() {
        return notificationDisabledPlayerSet;
    }

    public static boolean hasNotification(Player p) {
        return p.hasPermission("report.read") && !notificationDisabledPlayerSet.contains(p);
    }

    public static boolean disableNotification(Player p) {
        return notificationDisabledPlayerSet.add(p);
    }

    public static boolean enableNotification(Player p) {
        return notificationDisabledPlayerSet.remove(p);
    }

    public void broadcastReport(ReportData data) {
        Map<String, String> placeholder = data.getPlaceholder();
        Bukkit.getOnlinePlayers().stream()
                .filter(Report::hasNotification)
                .forEach(pl -> Messages.REPORT_BROADCAST.sendMessageIfExists(pl, placeholder));
    }

}
