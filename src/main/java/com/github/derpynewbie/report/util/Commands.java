package com.github.derpynewbie.report.util;

import com.github.derpynewbie.report.command.ReportCommand;
import com.github.derpynewbie.report.command.ShowReportCommand;
import com.github.derpynewbie.report.command.debug.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public enum Commands {
    REPORT(new ReportCommand(), "report", Messages.REPORT_PERMISSION_MESSAGE),
    SHOW_REPORT(new ShowReportCommand(), "showReport", Messages.SHOW_REPORT_PERMISSION_MESSAGE),
    DEBUG_GET_REPORT_DATA(new DebugGetReportData(), "getReportData", Messages.DEBUG_PERMISSION_MESSAGE),
    DEBUG_RELOAD_CONFIG(new DebugReloadConfig(), "reloadReportConfigs", Messages.DEBUG_PERMISSION_MESSAGE),
    DEBUG_RELOAD_MESSAGE(new DebugReloadMessage(), "reloadReportMessages", Messages.DEBUG_PERMISSION_MESSAGE),
    DEBUG_REMOVE_GARBAGE_DATA(new DebugRemoveGarbageData(), "removeGarbageData", Messages.DEBUG_PERMISSION_MESSAGE),
    DEBUG_REMOVE_REPORT_DATA(new DebugRemoveReportData(), "removeReportData", Messages.DEBUG_PERMISSION_MESSAGE),
    DEBUG_SAVE_REPORT_DATA(new DebugSaveReportData(), "saveReportData", Messages.DEBUG_PERMISSION_MESSAGE),
    ;

    private static boolean IS_INIT = false;

    private CommandExecutor executor;
    private String label;
    private Messages permissionMessage;

    Commands(CommandExecutor executor, String label, Messages permissionMessage) {
        this.executor = executor;
        this.label = label;
        this.permissionMessage = permissionMessage;
    }

    public static void reloadCommands(JavaPlugin pl) {
        pl.getLogger().info("Reloading commands.");
        if (!Messages.isInitialized())
            Messages.reloadMessage(pl);
        for (Commands command :
                values()) {
            pl.getLogger().fine("Loading command: " + command.label);
            PluginCommand plCommand = pl.getCommand(command.label);
            if (plCommand != null) {
                plCommand.setExecutor(command.executor);
                plCommand.setPermissionMessage(command.permissionMessage.getMessage());
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
