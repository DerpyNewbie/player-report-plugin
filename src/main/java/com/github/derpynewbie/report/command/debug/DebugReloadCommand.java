package com.github.derpynewbie.report.command.debug;

import com.github.derpynewbie.report.Report;
import com.github.derpynewbie.report.util.Commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DebugReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        commandSender.sendMessage("Reloading commands...");
        Commands.reloadCommands(Report.getInstance());
        commandSender.sendMessage("Successfully reloaded commands.");

        return true;
    }
}
