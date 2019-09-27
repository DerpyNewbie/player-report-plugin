package com.github.derpynewbie.report.command.debug;

import com.github.derpynewbie.report.Report;
import com.github.derpynewbie.report.util.ReportData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DebugRemoveReportData implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length <= 0) {
            commandSender.sendMessage("Not enough arguments.");
            return false;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(strings[0]);
        Integer index;
        ArrayList<ReportData> reportList;

        try {
            commandSender.sendMessage("Parsing number from argument");
            index = Integer.valueOf(strings[1]);

        } catch (NumberFormatException ex1) {
            commandSender.sendMessage(ex1.getMessage());
            return true;
        }
        try {
            commandSender.sendMessage(String.format("Found number %d", index));
            commandSender.sendMessage(String.format("Removing report of User: %s, UUID: %s, Index of: %d", player.getName(), player.getUniqueId().toString(), index));
            Report.getInstance().getLogger().warning(String.format("Removing report data of: User: %s, UUID: %s, Index of: %d", player.getName(), player.getUniqueId().toString(), index));
            Report.getInstance().getHelper().removeReportData(player, index);
            commandSender.sendMessage("Successfully removed report data.");
            Report.getInstance().getLogger().info("Successfully removed report data with debug command.");
            return true;
        } catch (NullPointerException |
                ArrayIndexOutOfBoundsException ex2) {
            commandSender.sendMessage(ex2.getMessage());
            Report.getInstance().getLogger().warning(ex2.getMessage());
            ex2.printStackTrace();
            return true;
        }

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
