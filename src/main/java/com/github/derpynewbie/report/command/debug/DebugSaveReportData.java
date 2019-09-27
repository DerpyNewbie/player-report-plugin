package com.github.derpynewbie.report.command.debug;

import com.github.derpynewbie.report.Report;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DebugSaveReportData implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Report.getInstance().getLogger().info("Manually saving report data...");
        commandSender.sendMessage("Saving report data...");
        if (Report.getInstance().getHelper().saveDataConfig()) {
            commandSender.sendMessage("Successfully saved report data.");
            Report.getInstance().getLogger().info("Successfully saved report data.");
        } else {
            commandSender.sendMessage("Could not save report data. check console.");
        }
        return true;
    }
}
