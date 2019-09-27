package com.github.derpynewbie.report.command.debug;

import com.github.derpynewbie.report.Report;
import com.github.derpynewbie.report.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DebugReloadMessage implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        commandSender.sendMessage("Reloading message.yml...");
        Messages.reloadMessage(Report.getInstance());
        commandSender.sendMessage("Successfully reloaded message.yml.");

        return true;
    }
}
