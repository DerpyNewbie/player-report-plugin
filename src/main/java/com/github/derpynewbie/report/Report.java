package com.github.derpynewbie.report;

import com.github.derpynewbie.report.command.ReportCommand;
import com.github.derpynewbie.report.command.ShowReportCommand;
import com.github.derpynewbie.report.command.debug.*;
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

        registerCommands();

        Runnable autoSaveTask = () -> repHelper.saveDataConfig();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, autoSaveTask, 20 * 60, 20 * 60 * 5);

    }

    public void setupConfig() {
        saveDefaultConfig();
        reloadConfig();
    }

    private void registerCommands() {
        // Begin Commands
        getCommand("report").setExecutor(new ReportCommand());
        getCommand("showReports").setExecutor(new ShowReportCommand());
        // End Commands

        // Begin Debug Commands
        getCommand("saveReportData").setExecutor(new DebugSaveReportData());
        getCommand("getReportData").setExecutor(new DebugGetReportData());
        getCommand("removeReportData").setExecutor(new DebugRemoveReportData());
        getCommand("removeGarbageData").setExecutor(new DebugRemoveGarbageData());
        getCommand("reloadReportMessages").setExecutor(new DebugReloadMessage());
        getCommand("reloadReportConfigs").setExecutor(new DebugReloadConfig());
        // End Debug Commands
    }

    public ReportHelper getHelper() {
        return repHelper;
    }

}
