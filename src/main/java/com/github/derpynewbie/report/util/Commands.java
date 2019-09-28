package com.github.derpynewbie.report.util;

import com.github.derpynewbie.report.command.ReportCommand;
import com.github.derpynewbie.report.command.ShowReportCommand;
import com.github.derpynewbie.report.command.ToggleReportsCommand;
import com.github.derpynewbie.report.command.debug.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public enum Commands {
    REPORT(new ReportCommand(), "report", Messages.REPORT_PERMISSION_MESSAGE, PluginConfig.REPORT_ALIAS),
    SHOW_REPORT(new ShowReportCommand(), "showReports", Messages.SHOW_REPORT_PERMISSION_MESSAGE, PluginConfig.SHOW_REPORT_ALIAS),
    TOGGLE_REPORT(new ToggleReportsCommand(), "toggleReports", Messages.TOGGLE_REPORT_PERMISSION_MESSAGE, PluginConfig.TOGGLE_REPORT_ALIAS),
    DEBUG_GET_REPORT_DATA(new DebugGetReportData(), "_report_getReportData", Messages.DEBUG_PERMISSION_MESSAGE),
    DEBUG_RELOAD_COMMAND(new DebugReloadCommand(), "_report_reloadReportCommands", Messages.DEBUG_PERMISSION_MESSAGE),
    DEBUG_RELOAD_CONFIG(new DebugReloadConfig(), "_report_reloadReportConfigs", Messages.DEBUG_PERMISSION_MESSAGE),
    DEBUG_RELOAD_MESSAGE(new DebugReloadMessage(), "_report_reloadReportMessages", Messages.DEBUG_PERMISSION_MESSAGE),
    DEBUG_REMOVE_GARBAGE_DATA(new DebugRemoveGarbageData(), "_report_removeGarbageData", Messages.DEBUG_PERMISSION_MESSAGE),
    DEBUG_REMOVE_REPORT_DATA(new DebugRemoveReportData(), "_report_removeReportData", Messages.DEBUG_PERMISSION_MESSAGE),
    DEBUG_SAVE_REPORT_DATA(new DebugSaveReportData(), "_report_saveReportData", Messages.DEBUG_PERMISSION_MESSAGE),
    ;

    private static boolean IS_INIT = false;

    private CommandExecutor executor;
    private String label;
    private Messages permissionMessage;
    private PluginConfig aliasConfig;

    Commands(CommandExecutor executor, String label, Messages permissionMessage, PluginConfig alias) {
        this(executor, label, permissionMessage);
        this.aliasConfig = alias;
    }

    Commands(CommandExecutor executor, String label, Messages permissionMessage) {
        this.executor = executor;
        this.label = label;
        this.permissionMessage = permissionMessage;
        this.aliasConfig = PluginConfig.NULL;
    }

    public static void reloadCommands(JavaPlugin pl) {
        pl.getLogger().info("Reloading commands.");
        if (!PluginConfig.isInitialized())
            PluginConfig.reloadConfig(pl);
        if (!Messages.isInitialized())
            Messages.reloadMessage(pl);
        for (Commands command :
                values()) {
            pl.getLogger().fine("Loading command: " + command.label);
            PluginCommand plCommand = pl.getCommand(command.label);
            if (plCommand != null) {
                plCommand.setExecutor(command.executor);
                plCommand.setPermissionMessage(command.permissionMessage.getMessage());
                if (command.aliasConfig != PluginConfig.NULL)
                    plCommand.setAliases(command.aliasConfig.getStringList());
            } else {
                pl.getLogger().severe("FAILED TO LOAD COMMAND. PLEASE REPORT THIS TO THE AUTHOR OF THIS PLUGIN.");
            }
        }
        IS_INIT = true;
        pl.getLogger().info("Reload complete.");
    }

    public static boolean isInitialized() {
        return IS_INIT;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public String getLabel() {
        return label;
    }

    public Messages getPermissionMessage() {
        return permissionMessage;
    }
}
