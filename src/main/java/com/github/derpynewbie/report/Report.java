package com.github.derpynewbie.report;

import com.github.derpynewbie.report.util.Commands;
import com.github.derpynewbie.report.util.Messages;
import com.github.derpynewbie.report.util.PluginConfig;
import com.github.derpynewbie.report.util.ReportHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Report extends JavaPlugin implements CommandExecutor, Listener {

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

}
