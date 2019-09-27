package com.github.derpynewbie.report.command.debug;

import com.github.derpynewbie.report.Report;
import com.github.derpynewbie.report.util.ReportData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

public class DebugGetReportData implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        ArrayList<ReportData> reportList;

        if (strings.length <= 0) {
            commandSender.sendMessage("Start querying for all players");

            reportList = Report.getInstance().getHelper().getReportData();

        } else {
            OfflinePlayer player = Bukkit.getOfflinePlayer(strings[0]);

            commandSender.sendMessage(String.format("Start querying for User: %s, UUID: %s", strings[0], player.getUniqueId()));

            reportList = Report.getInstance().getHelper().getReportData(player.getUniqueId());
        }

        if (reportList.size() == 0) {
            commandSender.sendMessage("No data found.");
            return true;
        }

        commandSender.sendMessage("Sending data.");
        for (ReportData data :
                reportList) {
            commandSender.sendMessage("Index: " + reportList.indexOf(data));
            commandSender.sendMessage(" Reported Player: " + data.getReportedPlayer().getName());
            commandSender.sendMessage(" Reported by     : " + data.getSender().getName());
            commandSender.sendMessage(" Reason           : " + data.getReason());
            commandSender.sendMessage(" Timestamp        : " + new Date(data.getTime()).toString());
        }
        commandSender.sendMessage("All data sent.");
        return true;
    }
}
