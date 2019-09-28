package com.github.derpynewbie.report.command;

import com.github.derpynewbie.report.Report;
import com.github.derpynewbie.report.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ToggleReportsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            Messages.TOGGLE_REPORT_NO_CONSOLE.sendMessageIfExists(commandSender, null);
            return true;
        }
        Player sender = (Player) commandSender;
        Map<String, String> placeholder = Messages.getPlayerPlaceholder(sender);

        if (Report.hasNotification(sender)) {
            Report.disableNotification(sender);
            Messages.TOGGLE_REPORT_ON_DISABLE.sendMessageIfExists(sender, placeholder);
        } else {
            Report.enableNotification(sender);
            Messages.TOGGLE_REPORT_ON_ENABLE.sendMessageIfExists(sender, placeholder);
        }

        return true;
    }
}
