package com.github.derpynewbie.report.command.debug;

import com.github.derpynewbie.report.Report;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DebugRemoveGarbageData implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        commandSender.sendMessage("Removing garbage data...");
        Report.getInstance().getHelper().removeGarbageData();
        commandSender.sendMessage("Successfully removed garbage data.");

        return true;
    }
}
